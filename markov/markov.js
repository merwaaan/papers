$(function() {

	for(var i = 0; i < 1000; ++i)
		$('body').append(generateName()+'<br/>');
});

var alphabet = null;
var matrix = null;

var corpus = [
	'aergia',
	'aether',
	'agrius',
	'alcyoneus',
	'aphrodite',
	'apollo',
	'ares',
	'artemis',
	'asteria',
	'athena',
	'chronos',
	'clymene',
	'cronus',
	'demether',
	'dionysus',
	'eleos',
	'epimetheus',
	'erebos',
	'geryon',
	'hades',
	'helios',
	'hephaestus',
	'hera',
	'hermes',
	'hestia',
	'hyperion',
	'leto',
	'megapenthes',
	'pallas',
	'perses',
	'pontus',
	'poseidon',
	'prometheus',
	'ophion',
	'selene',
	'steropes',
	'styx',
	'talos',
	'typhon',
	'tartarus',
	'tethys',
	'thalassa',
	'theia',
	'themis',
	'uranus',
	'zeus'
];

function generateName() {

	if(!alphabet)
		alphabet = generateAlphabet();

	if(!matrix)
		matrix = generateTransitionMatrix();

	var name = '';

	// Choose a starting letter.
	currentChar = alphabet[Math.floor(Math.random() * alphabet.length)];
	name += currentChar;

	do {
		currentChar = roulette(currentChar);
		name += currentChar;
	} while(currentChar !== '')

	return name.charAt(0).toUpperCase() + name.slice(1);
}

function generateAlphabet() {

	alphabet = [];

	for(var i = 97; i < 123; ++i)
		alphabet.push(String.fromCharCode(i));

	alphabet.push('');

	return alphabet;
}

function generateTransitionMatrix() {

	matrix = {};

	for(var i = 0; i < alphabet.length; ++i) {

		matrix[alphabet[i]] = {};

		for(var j = 0; j < alphabet.length; ++j)
			matrix[alphabet[i]][alphabet[j]] = 0;
	}

	// Count.
	for(var i = 0; i < corpus.length; ++i)
		for(var j = 0; j < corpus[i].length; ++j) {

			var currentChar = corpus[i].charAt(j);
			var nextChar = corpus[i].charAt(j + 1);

			matrix[currentChar][nextChar] += 1;
		}

	// Normalize.
	for(var i in matrix) {

		var count = 0;
		for(var j in matrix[i])
			count += matrix[i][j];

		if(count > 0)
			for(var j in matrix[i])
				matrix[i][j] /= count;
	}

	console.log('transition matrix >', matrix);

	return matrix;
}

function roulette(char) {

	var transitions = matrix[char];

	var chars = [];
	var count = 0;
	for(var i in transitions) {
		chars.push(i);
		if(transitions[i] > 0)
			++count;
	}

	if(count == 0)
		return '';

	var wheel = [];
	for(var i = 0; i < chars.length; ++i) {
		wheel[i] = transitions[chars[i]];
		if(i > 0)
			wheel[i] += wheel[i - 1];
	}

	var x = Math.random();

	for(var i = 0; i < wheel.length; ++i)
		if(x <= wheel[i])
			return chars[i];
}
