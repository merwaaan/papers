import csv
import sys

dictionnary = {}

file = sys.argv[1]
data = open(file, 'rb');
reader = csv.reader(data);

# Jump the first line.
reader.next()

# Count
for row in csv.reader(data):

    value = row[1]
    steps = row[3]

    if value not in dictionnary:
        dictionnary[int(value)] = [int(steps)]
    else:
        dictionnary[int(value)].append(int(steps))

# Average
for value, steps in sorted(dictionnary.items()):
    print str(value) + ' ' + str(sum(steps) / len(steps))
