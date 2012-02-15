#!/bin/bash

python prepare_plots.py run-num-ghosts "nombre de fantômes" "nombre de pas"
python prepare_plots.py run-diffusion-rate "taux de diffusion (%)" "nombre de pas"
python prepare_plots.py run-evaporation-rate "taux d'évaporation (%)" "nombre de pas"

gnuplot run-num-ghosts.plot
gnuplot run-diffusion-rate.plot
gnuplot run-evaporation-rate.plot
