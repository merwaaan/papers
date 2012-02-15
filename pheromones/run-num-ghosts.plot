set terminal png
set output "run-num-ghosts.png"
set xlabel "nombre de fantômes"
set ylabel "nombre de pas"
plot "run-num-ghosts.dat" with lines
