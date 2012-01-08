all: report slides

report: report.tex
	pdflatex report.tex
	bibtex report.aux
	pdflatex report.tex
	pdflatex report.tex

slides: slides.tex
	pdflatex slides.tex
