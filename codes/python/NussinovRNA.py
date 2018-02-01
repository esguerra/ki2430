#!/usr/bin/env python
# encoding: utf-8
# NussinovRNA.py
# James Brown
# Nussinov Algorithm


import sys
import os
import string
import getopt

# sample setup
#rna1 ="GGGAAAUCC"
rna1 = ""
rows = 0; # len(rna1)+1
cols = 0; #len(rna1)+1
min_hairpin = 2;
complement_lookup = {"A":"U","U":"A","C":"G","G":"C"}

# method to print the matrix that is crated 
def print_matrix(matrix):
	global rows
	global cols
	for i in range(rows):
		print "\r";
		for j in range(cols):
			print  matrix[i][j],;
			print "  ",;
	print "\n";


# initialize the matrix with the rna nucleotides
def place_names(scores, rna1):
	scores[0][0] = " "
	for j in range(len(rna1)):
		scores[j+1][0] = rna1[j]
		scores[0][j+1] = rna1[j]
	return scores

# method to find complements for the RNA nucleotides
def complements(letter1, letter2):
	if complement_lookup[letter1] == letter2:
		return 1;
	else:
		return 0;


# setup the matrix and find the hairpin turns in the RNA sequence
def nussinov(matrix):
	global rna1
	global min_hairpin
	start = min_hairpin
	loop_len = len(rna1) + 1
	for x in range(start,loop_len):
		for y in range(x,loop_len):
			i = y - x + 1;
			caseone = matrix[i+1][y-1] + complements(matrix[i][0],matrix[0][y]);
			casetwo = matrix[i+1][y];
			casethree = matrix[i][y-1];
			four = [0];
			for k in range(i,x):
				four.append(matrix[i][k] + matrix[k+1][x]);
			casefour = max(four)
			matrix[i][y] = max(caseone,casetwo,casethree,casefour);
	return matrix


# back track through the matrix to find the turns
def back_track(matrix):
	global cols
	global rows
	x = cols - 1
	y = 1
	print "The number of predicted base pairs - S(i,j) = ", matrix[y][x]
	while (x >= cols/2) & (y != rows/2):
		left = matrix[y][x-1]
		down = matrix[y+1][x]
		#add count for base pairs and format
		#matrix[y][x] = 9
		print "base pair " + " y= "+ str(y)+ " " + matrix [0][y] + "   x = " + str(x) +" "+  matrix[x][0] 
		if down > left:
			# go down
			y= y + 1;
		else:
			# go diagonal
			y = y + 1;
			x = x - 1;

	
def main(argv):
	global rna1
	global cols
	global rows
	global min_hairpin
	seq_file = ""
	
	#print len(argv)
	for i in range (len(argv)):
		if argv[i] == "-s":
			seq_file = argv[i+1];
		if argv[i] == "-h":
			min_hairpin = int(argv[i+1])
	if seq_file == "":
		print "No sequence file, check README for usage"
		sys.exit(2) 
	rna1 = seq_file
	cols = len(rna1)+1;
	rows = len(rna1)+1;
	
	#set up matrix and initialize it all to 0
	scores = [[0 for i in range(cols)] for j in range(rows)]
	scores = place_names(scores, rna1)
	scores = nussinov(scores)
	back_track(scores)
	print_matrix(scores)
	


if __name__ == '__main__':
	main(sys.argv[1:])
