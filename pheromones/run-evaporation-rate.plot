set terminal png
set output "run-evaporation-rate.png"
set xlabel "taux d'évaporation (%)"
set ylabel "nombre de pas"
plot "run-evaporation-rate.dat" with lines