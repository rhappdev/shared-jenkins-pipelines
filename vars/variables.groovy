def save(name, var) {
  writeFile file: "${name}", text: "${var}"
  stash name: "${name}", includes: "${name}"
}

def load(name) {
  unstash name: "${name}"
  readFile file: "${name}"
}



