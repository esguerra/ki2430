#!/bin/bash
#rm bibliography*blx*
#rm *.aux *.xml
pdflatex bibliography
bibtex bibliography1-blx
bibtex bibliography2-blx
bibtex bibliography3-blx
bibtex bibliography4-blx
bibtex bibliography5-blx
bibtex bibliography6-blx
pdflatex bibliography
pdflatex bibliography
pdflatex bibliography
evince bibliography.pdf &

