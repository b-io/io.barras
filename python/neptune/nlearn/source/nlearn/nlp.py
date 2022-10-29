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
#    Copyright © 2013-2022 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from gensim.utils import tokenize
from tensorflow.keras.layers import Activation, Dense, Dropout, Embedding, Input, LSTM
from tensorflow.keras.models import Model

from nlearn.common import *

####################################################################################################
# NLP PROPERTIES
####################################################################################################

__NLP_PROPERTIES__________________________________ = ''

WORD_VECTOR_PATH = LEARN_PROPS.get('wordVectorPath')

####################################################################################################
# NLP CONSTANTS
####################################################################################################

__NLP_CONSTANTS___________________________________ = ''

# The default maximum number of words
DEFAULT_MAX_WORD_COUNT = 20

####################################################################################################
# NLP CLASSES
####################################################################################################

__NLP_CLASSES_____________________________________ = ''


class WordEmbeddings:
	'''
	A handler for word embeddings.
	'''

	def __init__(self, path=WORD_VECTOR_PATH, encoding=DEFAULT_ENCODING, ignore=True, newline=None,
	             size=None, verbose=VERBOSE, verbose_interval=100000):
		'''
		Constructs a handler for word embeddings containing a vocabulary of words and their
		pre-trained word vectors.

		:param path: the path to the file containing the vocabulary words and their pre-trained word
		             vectors
		:param size: the size of the word vectors
		'''
		self.vocabulary = []
		self.word_vectors = []
		self.word_to_index = {}  # the dictionary mapping every vocabulary word to its index
		self.size = size

		if not is_empty(path):
			self.load(path, encoding=encoding, ignore=ignore, newline=newline, size=size,
			          verbose=verbose, verbose_interval=verbose_interval)

	##############################################

	def get_size(self):
		'''Returns the size of the word vectors.'''
		if not is_null(self.size):
			return self.size
		return self.word_vectors[0].shape[0]

	##############################################

	def sentence_to_word_indices(self, sentence, max_word_count=DEFAULT_MAX_WORD_COUNT):
		'''
		Converts the specified sentence to a list of word indices.

		:param sentence:       a sentence
		:param max_word_count: the maximum number of words in a sentence

		:return: a list of word indices and the set of unknown words
		'''
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
		'''
		Converts the specified sentences of size m to an array of word indices of shape
		(m x max_word_count).

		:param sentences:      a list of sentences of size m
		:param max_word_count: the maximum number of words in a sentence

		:return: an array of word indices of shape (m x max_word_count) and the set of unknown words
		'''
		word_indices = np.zeros((len(sentences), max_word_count), dtype=INT_ELEMENT_TYPE)
		unknown_words = set()
		for i, sentence in enumerate(sentences):
			# Get the word indices of the sentence
			(sentence_word_indices,
			 sentence_unknown_words) = self.sentence_to_word_indices(sentence,
			                                                         max_word_count=max_word_count)
			word_indices[i, :min(len(sentence_word_indices), max_word_count)] = sentence_word_indices
			unknown_words = unknown_words.union(sentence_unknown_words)
		return word_indices, unknown_words

	#####################

	def sentence_to_word_vectors(self, sentence, max_word_count=DEFAULT_MAX_WORD_COUNT):
		'''
		Converts the specified sentence to a list of word vectors.

		:param sentence:       a sentence
		:param max_word_count: the maximum number of words in a sentence

		:return: a list of word vectors and the set of unknown words
		'''
		# Get the word indices of the sentence
		(word_indices,
		 unknown_words) = self.sentence_to_word_indices(sentence,
		                                                max_word_count=max_word_count)
		# Get the corresponding word vectors
		return take_at(self.word_vectors, word_indices), unknown_words

	def sentences_to_word_vectors(self, sentences, max_word_count=DEFAULT_MAX_WORD_COUNT):
		'''
		Converts the specified sentences to a list of lists of word vectors.

		:param sentences:      a list of sentences
		:param max_word_count: the maximum number of words in a sentence

		:return: a list of lists of word vectors and the set of unknown words
		'''
		word_vectors = []
		unknown_words = set()
		for i, sentence in enumerate(sentences):
			# Get the word vectors of the sentence
			(sentence_word_vectors,
			 sentence_unknown_words) = self.sentence_to_word_vectors(sentence,
			                                                         max_word_count=max_word_count)
			word_vectors.append(sentence_word_vectors)
			unknown_words = unknown_words.union(sentence_unknown_words)
		return word_vectors, unknown_words

	#####################

	def sentence_to_single_word_vector(self, sentence, max_word_count=DEFAULT_MAX_WORD_COUNT):
		'''
		Converts the specified sentence to a single word vector.

		:param sentence:       a sentence
		:param max_word_count: the maximum number of words in a sentence

		:return: a single word vector and the set of unknown words
		'''
		# Get the word vectors of the sentence
		(word_vectors,
		 unknown_words) = self.sentence_to_word_vectors(sentence, max_word_count=max_word_count)
		# Sum the corresponding word vectors
		return sum(word_vectors), unknown_words

	def sentences_to_single_word_vectors(self, sentences, max_word_count=DEFAULT_MAX_WORD_COUNT):
		'''
		Converts the specified sentences to a list of single word vectors.

		:param sentences:      a list of sentences
		:param max_word_count: the maximum number of words in a sentence

		:return: a list of single word vectors and the set of unknown words
		'''
		word_vectors = []
		unknown_words = set()
		for i, sentence in enumerate(sentences):
			# Get the single word vector of the sentence
			(sentence_word_vector,
			 sentence_unknown_words) = self.sentence_to_single_word_vector(sentence,
			                                                               max_word_count=max_word_count)
			word_vectors.append(sentence_word_vector)
			unknown_words = unknown_words.union(sentence_unknown_words)
		return word_vectors, unknown_words

	##############################################

	def create_embedding_layer(self):
		'''
		Creates an embedding layer using the pre-trained word vectors.

		:return: an embedding layer using the pre-trained word vectors
		'''
		# Initialize the embedding matrix
		vocabulary_size = len(self.vocabulary) + 1  # add 1 to fit Keras embedding (requirement)
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

	def create_embedding_model(self, class_count, dropout_rate=0.5, hidden_unit_count=128,
	                           max_word_count=DEFAULT_MAX_WORD_COUNT):
		'''
		Creates a model with an embedding layer using the pre-trained word vectors that classifies
		the input sentences (encoded into arrays of word indices) into the output classes.

		:param class_count:       the number of output classes
		:param dropout_rate:      the fraction of the input units to drop
		:param hidden_unit_count: the number of hidden units in the LSTM layers
		:param max_word_count:    the maximum number of words in a sentence

		:return: a model with an embedding layer using the pre-trained word vectors that classifies
		         the input sentences (encoded into arrays of word indices) into the output classes
		'''
		# Create the input sentences (encoded into arrays of word indices)
		word_indices = Input(shape=(max_word_count,), dtype=INT_ELEMENT_TYPE)

		# Propagate the input through an embedding layer mapping every word index to its vector
		embeddings = self.create_embedding_layer()(word_indices)

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

		# Create the model that classifies the input sentences into the output classes
		return Model(inputs=[word_indices], outputs=classes)

	##############################################

	def find_closest_words(self, word_vector, top=10):
		a = distances(word_vector, self.word_vectors)
		return [self.vocabulary[i] for _, i in sort(zip(a, range(len(a))))[:top]]

	#####################

	def load(self, path, encoding=DEFAULT_ENCODING, ignore=True, newline=None, size=None,
	         verbose=VERBOSE, verbose_interval=100000):
		'''
		Loads the dictionary mapping the vocabulary words to their pre-trained word vectors.

		:param path: the path to the file containing the vocabulary words and their pre-trained word
		             vectors
		:param size: the size of the word vectors
		'''
		# Create the dictionary mapping every vocabulary word to its vector
		for i, line in read_enumerator(path, encoding=encoding, ignore=ignore, newline=newline):
			line = line.strip().split()
			if is_null(size):
				size = len(line) - 1
			if verbose and i % verbose_interval == 0:
				debug('Load the', str(size) + '-dimensional pre-trained word vectors',
				      'from', i + 1, 'to', i + verbose_interval, '...')
			word = paste(line[:-size])
			self.vocabulary.append(word)
			self.word_vectors.append(to_array(line[-size:], type=FLOAT_ELEMENT_TYPE))
		# Create the dictionary mapping every vocabulary word to its index
		for i, word in enumerate(self.vocabulary):
			self.word_to_index[word] = i
