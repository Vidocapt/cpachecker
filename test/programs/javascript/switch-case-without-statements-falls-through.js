function __VERIFIER_error() {}

function switchTest(value){
  var result = 0;

  switch(value) {
    case 0:
    case 1:
      result += 4;
      break;
    case 2:
      result += 8;
  }

  return result;
}
        
if(!(switchTest(0) === 4)){
  __VERIFIER_error();
}

if(!(switchTest(1) === 4)){
  __VERIFIER_error();
}

if(!(switchTest(2) === 8)){
  __VERIFIER_error();
}

if(!(switchTest(3) === 0)){
  __VERIFIER_error();
}

if(!(switchTest(4) === 0)){
  __VERIFIER_error();
}

if(!(switchTest(true) === 0)){
  __VERIFIER_error();
}

if(!(switchTest(false) === 0)){
  __VERIFIER_error();
}

if(!(switchTest(null) === 0)){
  __VERIFIER_error();
}

if(!(switchTest(void 0) === 0)){
  __VERIFIER_error();
}

if(!(switchTest('0') === 0)){
  __VERIFIER_error();
}