// Takes a numeric value and returns a string
isOdd = (numeric n) => {
  if ( n % 2 == 0 ) {
    return 'even';
  } else {
    return 'odd';
  }
};
// returns 'odd'
dump(isOdd(1));