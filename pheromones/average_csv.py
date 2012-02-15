import csv

dictionnary = {}

data = open('run-num-ghosts.csv', 'rb');
reader = csv.reader(data);

# Jump the first line.
reader.next()

# Count
for row in csv.reader(data):

    value = row[1]
    steps = row[3]

    if value not in dictionnary:
        dictionnary[value] = [int(steps)]
    else:
        dictionnary[value].append(int(steps))

# Average
for value, steps in dictionnary.items():
    print str(value) + ' ' + str(steps)
    print str(value) + ' ' + str(sum(steps) / len(steps))

