const desplazamiento = document.getElementById("desplazamiento");
const texto = document.getElementById("texto");
const textocifrado = document.getElementById("cifrado");

function cifrado() {
    const textoingresado = texto.value;

    textocifrado.value = textoingresado.split('').map(c => {
        let mayus = (c === c.toUpperCase()) ? true : false;
        let valorEntero = c.toLowerCase().charCodeAt(0);
        if (valorEntero >= 97 && valorEntero <= 122) {
            // Son letras, entonces las cifro
            const valordesplazamiento = parseInt(desplazamiento.value);
            let nuevoValorEntero = valorEntero + valordesplazamiento;
            if (nuevoValorEntero > 122) {
                nuevoValorEntero = 97 + (nuevoValorEntero - 123);
            }
            valorEntero = nuevoValorEntero;
        }
        let cifrado = String.fromCharCode(valorEntero);
        return mayus ? cifrado.toUpperCase() : cifrado;
    }).join('');
}

texto.addEventListener("keyup", cifrado); 
desplazamiento.addEventListener("input", cifrado); 

desplazamiento.addEventListener("input", function() {
    document.getElementById("valorDesplazamiento").textContent = this.value;
});