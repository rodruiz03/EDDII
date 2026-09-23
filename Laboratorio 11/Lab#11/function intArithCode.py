Variables principales:
k → número de bits que vamos a usar internamente (precisión).
R = 2^k → rango total de trabajo. El intervalo [0, R-1] representa todo el espacio.
n → longitud del mensaje (número de símbolos).
l (low) → límite inferior del intervalo actual.
u (upper) → límite superior del intervalo actual.
s = u - l + 1 → tamaño actual del intervalo.
fi(v) → frecuencia acumulada hasta el símbolo v.
T → suma total de frecuencias (normalizador).
m → contador de underflow bits: bits diferidos que se emitirán más adelante cuando se resuelva la ambigüedad.

Símbolos:
    mj   c   f(i)   f(i+1)
    1   1       0       1
    2   10      1       11
    3   20      11      31
    T   31   

k = 8 // definido por nosotros
R = 2 a la 8 = 256
R debe ser mayor que 4T
mensaje 3212
n = 4 // tamaño del mensaje

function IntArithCode(mensaje, k, n)
    R = power(2,k)           // rango entero disponible [0, R-1]
    l = 0                    // límite inferior del intervalo
    u = R − 1                // límite superior del intervalo
    m = 0                    // contador de underflow (bits diferidos)
    
    for i = 1 to n           // recorrer cada símbolo del mensaje
        s = u − l + 1        // tamaño actual del intervalo
        u = l + floor(s * fi(vi+1) / T) − 1   // actualizar límite superior
        l = l + floor(s * fi(vi) / T)         // actualizar límite inferior
        
        // Renormalización: se revisa en qué parte está el intervalo
        while true
            if (l >= R/2)              // intervalo completamente en la mitad superior
                WriteBit(1)            // el siguiente bit de salida es 1
                u = 2*u − R + 1        // reescalar a nuevo rango
                l = 2*l − R
                for j = 1 to m WriteBit(0) // emitir los underflow diferidos como ceros
                m = 0
            else if (u < R/2)          // intervalo completamente en la mitad inferior
                WriteBit(0)            // el siguiente bit de salida es 0
                u = 2*u + 1            // reescalar a nuevo rango
                l = 2*l
                for j = 1 to m WriteBit(1) // emitir underflow diferidos como unos
                m = 0
            else if (l >= R/4 and u < 3*R/4) // intervalo en la mitad intermedia
                u = 2*u − R/2 + 1      // aún no se decide, reescalar
                l = 2*l − R/2
                m = m + 1              // acumular un underflow (bit diferido)
            else 
                continue                // salir del while cuando el intervalo está bien posicionado
        end while
    end for

    // Terminación: generar bits finales
    if (l >= R/4) 
        WriteBit(1)
        for j = 1 to m WriteBit(0)     // si se habían diferido, ponerlos como 0
        WriteBit(0)                     // bit final para cerrar
    else
        WriteBit(0)
        for j = 1 to m WriteBit(1)     // si se habían diferido, ponerlos como 1
        WriteBit(1)                     // bit final para cerrar
end function