#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide machine learning utilities for natural language processing (NLP).
########################################################################################################################

from __future__ import annotations

import logging

from gensim.utils import tokenize
from tensorflow.keras.layers import Activation, Dense, Dropout, Embedding, Input, LSTM
from tensorflow.keras.models import Model

from nlearn.common import *
from nutil.io.file import read_enumerator
from nutil.math import *
from nutil.struct.util import sort, take_at

__NLP_CONSTANTS___________________________________________________________________________ = ""


### DEFAULTS ###############################################

# The default maximum number of words
DEFAULT_MAX_WORD_COUNT: int = 20


__NLP_CLASSES_____________________________________________________________________________ = ""


class WordEmbeddings:
    """
    A handler for word embeddings.
    """

    def __init__(
        self,
        path=WORD_VECTOR_PATH,
        size=None,
        *,
        # Read
        encoding: str = DEFAULT_ENCODING,
        ignore=True,
        newline=None,
        # Log
        verbose=VERBOSE,
        verbose_interval=100000,
    ):
        """
        Constructs a handler for word embeddings containing a vocabulary of words and their pre-trained word vectors.

        Args:
            path: The path to the file containing the vocabulary words and their pre-trained word vectors.
            size: The size of the word vectors.
        """
        self.vocabulary = []
        self.word_vectors = []
        self.word_to_index = {}  # the dictionary mapping every vocabulary word to its index
        self.size = size

        if not is_empty(path):
            self.load(
                path,
                # Read
                encoding=encoding,
                ignore=ignore,
                newline=newline,
                size=size,
                # Log
                verbose=verbose,
                verbose_interval=verbose_interval,
            )

    #### ACCESSORS #########################################

    def get_size(self):
        """Returns the size of the word vectors."""
        if not is_null(self.size):
            return self.size
        return self.word_vectors[0].shape[0]

    #### BUILDERS ##########################################

    def build_embedding_layer(self):
        """
        Builds an embedding layer using the pre-trained word vectors.

        Returns:
            An embedding layer using the pre-trained word vectors.
        """
        # Initialize the embedding matrix
        vocabulary_size = len(self.vocabulary) + 1  # adds 1 to fit Keras embedding (requirement)
        embedding_size = self.get_size()
        embedding_matrix = np.zeros((vocabulary_size, embedding_size), dtype=FLOAT_ELEMENT_TYPE)

        # Set every row of the embedding matrix to be the word vector of the ith vocabulary word
        for i, word_vector in enumerate(self.word_vectors):
            embedding_matrix[i, :] = word_vector

        # Create the embedding layer with the corresponding input and output sizes (non-trainable)
        embedding_layer = Embedding(vocabulary_size, embedding_size, trainable=False)

        # Build the embedding layer
        embedding_layer.build((None,))

        # Set the weights of the embedding layer to the embedding matrix
        embedding_layer.set_weights([embedding_matrix])

        return embedding_layer

    def build_embedding_model(
        self,
        class_count,
        dropout_rate=0.5,
        hidden_unit_count=128,
        max_word_count=DEFAULT_MAX_WORD_COUNT,
    ):
        """
        Creates a model with an embedding layer using the pre-trained word vectors that classifies
        the input sentences (encoded into arrays of word indices) into the output classes.

        Args:
            class_count: The number of output classes.
            dropout_rate: The fraction of the input units to drop.
            hidden_unit_count: The number of hidden units in the LSTM layers.
            max_word_count: The maximum number of words in a sentence.

        Returns:
            A model with an embedding layer using the pre-trained word vectors that classifies the input sentences
            (encoded into arrays of word indices) into the output classes.
        """
        # Create the input sentences (encoded into arrays of word indices)
        word_indices = Input(shape=(max_word_count,), dtype=INT_ELEMENT_TYPE)

        # Propagate the input through an embedding layer mapping every word index to its vector
        embeddings = self.create_embedding_layer()(word_indices)

        # Propagate the embeddings through an LSTM layer that returns a batch of sequences
        X = LSTM(hidden_unit_count, return_sequences=True)(embeddings)
        # Add a dropout layer
        X = Dropout(dropout_rate)(X)

        # Propagate `X` through another LSTM layer that returns a single hidden state
        X = LSTM(hidden_unit_count, return_sequences=False)(X)
        # Add a dropout layer
        X = Dropout(dropout_rate)(X)

        # Propagate `X` through a dense layer
        X = Dense(class_count, activation=None)(X)
        # Add a softmax activation
        classes = Activation("softmax")(X)

        # Create the model that classifies the input sentences into the output classes
        return Model(inputs=[word_indices], outputs=classes)

    #### CONVERTERS ########################################

    def sentence_to_word_indices(self, sentence, max_word_count=DEFAULT_MAX_WORD_COUNT):
        """
        Converts the specified sentence to a `list` of word indices.

        Args:
            sentence: A sentence.
            max_word_count: The maximum number of words in a sentence.

        Returns:
            A `list` of word indices and the set of unknown words.
        """
        word_indices = []
        unknown_words = set()
        sentence_words = tokenize(sentence, lowercase=True)
        # Convert every sentence word to its index in the vocabulary
        for i, word in enumerate(sentence_words):
            if i >= max_word_count:
                return word_indices, unknown_words
            if word in self.word_to_index:
                word_indices.append(self.word_to_index[word])
            else:
                unknown_words.add(word)
        return word_indices, unknown_words

    def sentences_to_word_indices(self, sentences, max_word_count=DEFAULT_MAX_WORD_COUNT):
        """
        Converts the specified sentences of size m to an array of word indices of shape
        (`m` × `max_word_count`).

        Args:
            sentences: A `list` of sentences of size `m`.
            max_word_count: The maximum number of words in a sentence.

        Returns:
            An array of word indices of shape (`m` × `max_word_count`) and the set of unknown words.
        """
        word_indices = np.zeros((len(sentences), max_word_count), dtype=INT_ELEMENT_TYPE)
        unknown_words = set()
        for i, sentence in enumerate(sentences):
            # Get the word indices of the sentence
            (sentence_word_indices, sentence_unknown_words) = self.sentence_to_word_indices(
                sentence,
                max_word_count=max_word_count,
            )
            word_indices[i, : min(len(sentence_word_indices), max_word_count)] = sentence_word_indices
            unknown_words = unknown_words.union(sentence_unknown_words)
        return word_indices, unknown_words

    ##########################

    def sentence_to_word_vectors(self, sentence, max_word_count=DEFAULT_MAX_WORD_COUNT):
        """
        Converts the specified sentence to a `list` of word vectors.

        Args:
            sentence: A sentence.
            max_word_count: The maximum number of words in a sentence.

        Returns:
            A `list` of word vectors and the set of unknown words.
        """
        # Get the word indices of the sentence
        (word_indices, unknown_words) = self.sentence_to_word_indices(sentence, max_word_count=max_word_count)
        # Get the corresponding word vectors
        return take_at(self.word_vectors, word_indices), unknown_words

    def sentences_to_word_vectors(self, sentences, max_word_count=DEFAULT_MAX_WORD_COUNT):
        """
        Converts the specified sentences to a `list` of lists of word vectors.

        Args:
            sentences: A `list` of sentences.
            max_word_count: The maximum number of words in a sentence.

        Returns:
            A `list` of lists of word vectors and the set of unknown words.
        """
        word_vectors = []
        unknown_words = set()
        for i, sentence in enumerate(sentences):
            # Get the word vectors of the sentence
            (sentence_word_vectors, sentence_unknown_words) = self.sentence_to_word_vectors(
                sentence,
                max_word_count=max_word_count,
            )
            word_vectors.append(sentence_word_vectors)
            unknown_words = unknown_words.union(sentence_unknown_words)
        return word_vectors, unknown_words

    ##########################

    def sentence_to_single_word_vector(self, sentence, max_word_count=DEFAULT_MAX_WORD_COUNT):
        """
        Converts the specified sentence to a single word vector.

        Args:
            sentence: A sentence.
            max_word_count: The maximum number of words in a sentence.

        Returns:
            A single word vector and the set of unknown words.
        """
        # Get the word vectors of the sentence
        (word_vectors, unknown_words) = self.sentence_to_word_vectors(sentence, max_word_count=max_word_count)
        # Sum the corresponding word vectors
        return sum(word_vectors), unknown_words

    def sentences_to_single_word_vectors(self, sentences, max_word_count=DEFAULT_MAX_WORD_COUNT):
        """
        Converts the specified sentences to a `list` of single word vectors.

        Args:
            sentences: A `list` of sentences.
            max_word_count: The maximum number of words in a sentence.

        Returns:
            A `list` of single word vectors and the set of unknown words.
        """
        word_vectors = []
        unknown_words = set()
        for i, sentence in enumerate(sentences):
            # Get the single word vector of the sentence
            (sentence_word_vector, sentence_unknown_words) = self.sentence_to_single_word_vector(
                sentence,
                max_word_count=max_word_count,
            )
            word_vectors.append(sentence_word_vector)
            unknown_words = unknown_words.union(sentence_unknown_words)
        return word_vectors, unknown_words

    #### FINDERS ###########################################

    def find_closest_words(self, word_vector, top=10):
        a = distances(word_vector, self.word_vectors)
        return [self.vocabulary[i] for _, i in sort(zip(a, range(len(a))))[:top]]

    #### LOADERS ###########################################

    def load(
        self,
        path=WORD_VECTOR_PATH,
        size=None,
        *,
        # Read
        encoding: str = DEFAULT_ENCODING,
        ignore=True,
        newline=None,
        # Log
        verbose=VERBOSE,
        verbose_interval=100000,
    ):
        """
        Loads the dictionary mapping the vocabulary words to their pre-trained word vectors.

        Args:
            path: The path to the file containing the vocabulary words and their pre-trained word vectors.
            size: The size of the word vectors.
        """
        # Create the dictionary mapping every vocabulary word to its vector
        for i, line in read_enumerator(path, encoding=encoding, ignore=ignore, newline=newline):
            line = line.strip().split()
            if is_null(size):
                size = len(line) - 1
            if verbose and i % verbose_interval == 0:
                logging.debug(
                    "Load the",
                    str(size) + "-dimensional pre-trained word vectors",
                    "from",
                    i + 1,
                    "to",
                    i + verbose_interval,
                    ELLIPSIS,
                )
            word = paste(line[:-size])
            self.vocabulary.append(word)
            self.word_vectors.append(to_array(line[-size:], element_type=FLOAT_ELEMENT_TYPE))
        # Create the dictionary mapping every vocabulary word to its index
        for i, word in enumerate(self.vocabulary):
            self.word_to_index[word] = i
