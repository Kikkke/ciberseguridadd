const texto = document.getElementById("texto");
const clave = document.getElementById("clave");
const textocifrado = document.getElementById("cifrado");
const textodescifrado = document.getElementById("descifrado");
const btnCifrar = document.getElementById("btnCifrar");
const btnDescifrar = document.getElementById("btnDescifrar");

const abc = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"];
const longitudAbc = abc.length;

function esValido(texto) {
    for (let c of texto) {
        if (!abc.includes(c.toLowerCase()) && c !== " ") {
            return false;
        }
    }
    return true;
}

function procesarTexto(txt, desp, action) {
    const replace = function(c) {
        const esMayuscula = c === c.toUpperCase();
        const i = abc.indexOf(c.toLowerCase());
        if (i !== -1) {
            let pos = i;
            if (action === "cifrar") {
                pos += desp;
                pos = pos >= longitudAbc ? pos - longitudAbc : pos;
            } else if (action === "descifrar") {
                pos -= desp;
                pos = pos < 0 ? pos + longitudAbc : pos;
            }
            const caracter = abc[pos];
            return esMayuscula ? caracter.toUpperCase() : caracter;
        }
        return c;
    };
    return txt.split("").map(c => replace(c)).join("");
}

btnCifrar.addEventListener("click", function() {
    const textoIngresado = texto.value;
    const claveIngresada = clave.value.toLowerCase();
    if (textoIngresado.trim() === "" || claveIngresada.trim() === "") {
        alert("Por favor no dejes campos vacios :((");
        return;
    }
    if (!esValido(textoIngresado)) {
        alert("El texto contiene caracteres inválidos.");
        return;
    }
    if (!esValido(claveIngresada)) {
        alert("La clave contiene caracteres inválidos.");
        return;
    }
    let resultado = "";
    for (let i = 0, j = 0; i < textoIngresado.length; i++) {
        const caracter = textoIngresado[i];
        if (abc.includes(caracter.toLowerCase())) {
            const desp = abc.indexOf(claveIngresada[j % claveIngresada.length]);
            resultado += procesarTexto(caracter, desp, "cifrar");
            j++;
        } else {
            resultado += caracter;
        }
    }
    textocifrado.value = resultado;
});

btnDescifrar.addEventListener("click", function() {
    const textoCifrado = textocifrado.value;
    const claveIngresada = clave.value.toLowerCase();
    if (textoCifrado.trim() === "" || claveIngresada.trim() === "") {
        alert("Los campos de texto cifrado y clave no pueden estar vacíos.");
        return;
    }
    if (!esValido(textoCifrado)) {
        alert("El texto cifrado contiene caracteres inválidos.");
        return;
    }
    if (!esValido(claveIngresada)) {
        alert("La clave contiene caracteres inválidos.");
        return;
    }
    let resultado = "";
    for (let i = 0, j = 0; i < textoCifrado.length; i++) {
        const caracter = textoCifrado[i];
        if (abc.includes(caracter.toLowerCase())) {
            const desp = abc.indexOf(claveIngresada[j % claveIngresada.length]);
            resultado += procesarTexto(caracter, desp, "descifrar");
            j++;
        } else {
            resultado += caracter;
        }
    }
    textodescifrado.value = resultado;
});