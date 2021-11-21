#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain machine learning utility functions for natural language processing (NLP)
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2021 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from nutil.common import *
from gensim.utils import tokenize
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Activation, Dense, Dropout, Embedding, Input, LSTM

####################################################################################################
# NLP CONSTANTS
####################################################################################################

__NLP_CONSTANTS___________________________________ = ''

DEFAULT_MAX_WORD_COUNT = 20

####################################################################################################
# NLP CLASSES
####################################################################################################

__NLP_CLASSES_____________________________________ = ''


class GloVe:
	"""
	Global Vectors for Word Representation (GloVe) by Jeffrey Pennington, Richard Socher, and
	Christopher D. Manning (2014).
	"""

	def __init__(self, path=None):
		self.vocabulary = list()
		self.word_to_vector = {}  # the dictionary mapping every vocabulary word to its word vector
		self.word_to_index = {}  # the dictionary mapping every vocabulary word to its index
		self.index_to_word = {}  # the dictionary mapping every index to its vocabulary word

		if not is_null(path):
			self.load(path)

	##############################################

	def sentences_to_indices(self, sentences, max_word_count=DEFAULT_MAX_WORD_COUNT):
		"""
		Converts the specified array of sentences of shape (m) to an array of vocabulary word
		indices of shape (m x max_word_count).

		:param sentences:      an array of sentences of shape (m)
		:param max_word_count: the maximum number of words in a sentence

		:return: an array of vocabulary word indices of shape (m x max_word_count)
		"""
		indices = np.zeros((len(sentences), max_word_count))
		for i, sentence in enumerate(sentences):
			sentence_words = tokenize(sentence, lowercase=True)
			# Convert every sentence word to its index in the vocabulary
			for j, word in enumerate(sentence_words):
				if word in self.word_to_index:
					indices[i, j] = self.word_to_index[word]
				else:
					warn('Unknown word:', quote(word))
		return indices

	##############################################

	def create_embedding_layer(self):
		"""
	    Creates an embedding layer using the pre-trained word vectors.

	    :return: an embedding layer using the pre-trained word vectors
	    """

		# Initialize the embedding matrix
		vocabulary_size = len(self.vocabulary) + 1  # add 1 to fit Keras embedding (requirement)
		embedding_size = self.word_to_vector[self.vocabulary[0]].shape[0]  # the dimensionality of the word vectors
		embedding_matrix = np.zeros((vocabulary_size, embedding_size))

		# Set every row of the embedding matrix to be the word vector of the ith vocabulary word
		for i, word in self.index_to_word.items():
			embedding_matrix[i, :] = self.word_to_vector[word]

		# Create the embedding layer with the corresponding input and output sizes (non-trainable)
		embedding_layer = Embedding(vocabulary_size, embedding_size, trainable=False)

		# Build the embedding layer
		embedding_layer.build((None,))

		# Set the weights of the embedding layer to the embedding matrix
		embedding_layer.set_weights([embedding_matrix])

		return embedding_layer

	def create_embedding_model(self, class_count, dropout_rate=0.5, hidden_unit_count=128,
	                           max_word_count=DEFAULT_MAX_WORD_COUNT):
		"""
		Create a model with an embedding layer using the pre-trained word vectors that converts the
		input sentence indices to the estimated output classes.

		:param class_count:       the number of output classes
		:param dropout_rate:      the fraction of the input units to drop
		:param hidden_unit_count: the number of hidden units in the LSTM layers
		:param max_word_count:    the maximum number of words in a sentence

		:return: a model with an embedding layer using the pre-trained word vectors that converts
		         the input sentence indices to the estimated output classes
		"""
		# Create the input (sentence indices)
		sentence_indices = Input(shape=(max_word_count,), dtype='int32')

		# Propagate the input through an embedding layer created using the pre-trained word vectors
		embeddings = self.create_embedding_layer()(sentence_indices)

		# Propagate the embeddings through an LSTM layer that returns a batch of sequences
		X = LSTM(hidden_unit_count, return_sequences=True)(embeddings)
		# Add a dropout layer
		X = Dropout(dropout_rate)(X)

		# Propagate X trough another LSTM layer that returns a single hidden state
		X = LSTM(hidden_unit_count, return_sequences=False)(X)
		# Add a dropout layer
		X = Dropout(dropout_rate)(X)

		# Propagate X through a dense layer
		X = Dense(class_count, activation=None)(X)
		# Add a softmax activation
		classes = Activation('softmax')(X)

		# Create the model that converts the input sentence indices to the estimated output classes
		return Model(inputs=[sentence_indices], outputs=classes)

	##############################################

	def load(self, path):
		"""Loads the dictionary mapping the vocabulary words to their pre-trained word vectors."""
		with open(path, mode='r', encoding='utf8', errors='ignore') as f:
			# Create the dictionary mapping every vocabulary word to its word vector
			for line in f:
				line = line.strip().split()
				word = line[0]
				self.vocabulary.append(word)
				self.word_to_vector[word] = np.array(line[1:], dtype=np.float64)
		# Create the dictionary mapping every vocabulary word to its index, and vice versa
		sort(self.vocabulary, inplace=True)
		for i, w in enumerate(self.vocabulary):
			self.word_to_index[w] = i
			self.index_to_word[i] = w


####################################################################################################
# NLP FUNCTIONS
####################################################################################################

__NLP_____________________________________________ = ''


def to_one_hot(Y, size):
	"""
	Converts the specified array of vectors Y to an array of one-hot vectors of the specified size.

	:param Y:    an array of vectors
	:param size: the size of the one-hot vectors

	:return: an array of one-hot vectors of the specified size
	"""
	return np.eye(size)[Y.reshape(-1)]
