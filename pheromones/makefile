all: rapport slides

rapport: rapport.tex
	pdflatex rapport.tex
	bibtex rapport.aux
	pdflatex rapport.tex
	pdflatex rapport.tex

slides: slides.tex
	pdflatex slides.tex
