set terminal png
set output "run-diffusion-rate.png"
set xlabel "taux de diffusion (%)"
set ylabel "nombre de pas"
plot "run-diffusion-rate.dat" with lines
