import csv
import sys

dictionnary = {}

name = sys.argv[1]
xlabel = sys.argv[2]
ylabel = sys.argv[3]

source = open(name + '.csv', 'rb');
reader = csv.reader(source);

# Jump the first line.
reader.next()

# Count.
for row in reader:

    value = row[1]
    steps = row[3]

    if value not in dictionnary:
        dictionnary[int(value)] = [int(steps)]
    else:
        dictionnary[int(value)].append(int(steps))

# Average.
data = []
for value, steps in sorted(dictionnary.items()):
    data.append(str(sum(steps) / len(steps)))

# Write data file.
f = open(name + '.dat', 'w')
for index, steps in enumerate(data):
    f.write(str(index + 1) + ' ' + steps + "\n")
f.close()

# Write gnuplot file.
f = open(name + '.plot', 'w')
f.write('set terminal png\n')
f.write('set output "' + name + '.png"\n')
f.write('set xlabel "' + xlabel + '"\n')
f.write('set ylabel "' + ylabel + '"\n')
f.write('plot "' + name + '.dat" with lines\n')
f.close()
