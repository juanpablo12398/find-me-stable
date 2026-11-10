# Manual de Usuario - Find Me

Guía de uso del sistema de reporte y avistamiento de personas desaparecidas.

---

## Contenido

1. [Introducción](#introducción)
2. [Acceso al Sistema](#acceso-al-sistema)
3. [Registro como Avistador](#registro-como-avistador)
4. [Reportar una Persona Desaparecida](#reportar-una-persona-desaparecida)
5. [Reportar un Avistamiento](#reportar-un-avistamiento)
6. [Buscar Personas Desaparecidas](#buscar-personas-desaparecidas)
7. [Uso del Mapa Interactivo](#uso-del-mapa-interactivo)
8. [Visualización de Perfiles de Personas](#visualización-de-perfiles-de-personas)
9. [Preguntas Frecuentes](#preguntas-frecuentes)
10. [Soporte y Contacto](#soporte-y-contacto)
11. [Términos y Condiciones](#términos-y-condiciones)
12. [Política de Privacidad](#política-de-privacidad)

---

## Introducción

### ¿Qué es Find Me?

Find Me es una plataforma web gratuita que permite a ciudadanos y organizaciones:

- Reportar personas desaparecidas con información estructurada.
- Registrar avistamientos geolocalizados en tiempo casi real.
- Visualizar casos activos en un mapa interactivo.
- Buscar personas por DNI, nombre o apellido.
- Colaborar en la búsqueda durante las primeras 24—48 horas críticas.

### ¿Por qué es importante?

Estudios internacionales señalan que entre el 80 % y el 90 % de las personas desaparecidas son localizadas durante las primeras 48 horas. Find Me contribuye a:

- Centralizar información que hoy se encuentra dispersa (redes sociales, llamados telefónicos, mensajería).
- Reducir tiempos de respuesta.
- Facilitar la coordinación entre ciudadanos, organizaciones y autoridades.

### ¿Quién puede utilizar Find Me?

- **Familiares de personas desaparecidas:** Para reportar casos y recibir avistamientos.
- **Ciudadanos (avistadores):** Para reportar avistamientos de personas desaparecidas.
- **ONGs y organizaciones sociales:** Para coordinar búsquedas y centralizar información.
- **Autoridades competentes:** Para acceder a información geolocalizada y estructurada.

### ¿Tiene costo?

No. Find Me es de acceso público y gratuito.

---

## Acceso al Sistema

### Requisitos técnicos

- **Navegador web moderno:**
    - Google Chrome 90 o superior
    - Mozilla Firefox 88 o superior
    - Safari 14 o superior
    - Microsoft Edge 90 o superior

- **Conexión a Internet:** Se recomienda una velocidad mínima de 2 Mbps.

- **Dispositivos soportados:**
    - Computadoras de escritorio
    - Notebooks
    - Tablets
    - Smartphones (Android / iOS), mediante navegador móvil

### URL del sistema

- **Entorno de desarrollo (local):**  
  `http://localhost:8080`

- **Entorno de producción (referencia):**  
  La URL definitiva dependerá del servidor de despliegue (por ejemplo, `https://find-me-api.railway.app`). En la documentación de deployment se detalla cómo obtener y configurar dicha URL.

---

## Registro como Avistador

### Objetivo

El “avistador” es el usuario registrado que puede:

- Reportar personas desaparecidas.
- Registrar avistamientos geolocalizados.

### Requisitos previos

- Acceder a la aplicación web.
- Contar con un DNI válido.
- Ser mayor de 18 años.

### Pasos para el registro

1. Acceder a la página principal de Find Me.
2. Seleccionar la opción **“Registrarse como Avistador”** en el menú o en la pantalla inicial.
3. Completar el formulario de registro con los siguientes datos:
    - DNI
    - Nombre
    - Apellido
    - Edad
    - Correo electrónico
    - Teléfono (opcional)
    - Dirección (opcional)
4. Leer y aceptar los términos y condiciones.
5. Confirmar el envío del formulario.
6. El sistema valida los datos contra el padrón nacional simulado (RENAPER).
7. Si la validación es exitosa, se registra el avistador y se inicia sesión automáticamente.

![Formulario de Registro de Avistador](img/registro_avistador.png)

### Información requerida

| Campo      | Obligatorio | Descripción                                      |
|-----------|-------------|--------------------------------------------------|
| DNI       | Sí          | Número de documento nacional                     |
| Nombre    | Sí          | Nombre completo                                  |
| Apellido  | Sí          | Apellido completo                                |
| Edad      | Sí          | Debe ser mayor o igual a 18 años                 |
| Email     | Sí          | Correo electrónico válido                         |
| Teléfono  | No          | Teléfono de contacto                              |
| Dirección | No          | Domicilio actual                                  |

**Importante:**  
Los datos ingresados se validan contra el padrón nacional simulado. Deben coincidir para que el registro sea aceptado.

---

## Reportar una Persona Desaparecida

### Requisitos previos

- Estar registrado como avistador.
- Tener la sesión iniciada en el sistema.

### Acceso a la función

1. Desde el menú principal, seleccionar la opción **“Reportar persona desaparecida”**.
2. Se abrirá un formulario específico para la carga del caso.

![Pantalla para Reportar Persona Desaparecida](img/reportar_persona.png)

### Información a proporcionar

**Datos personales de la persona desaparecida:**

- DNI
- Nombre y apellido
- Edad aproximada
- Fotografía reciente (recomendado)

**Descripción y contexto:**

- Descripción física detallada (altura, contextura, color de cabello, ojos, marcas particulares).
- Última vestimenta conocida.
- Fecha y lugar aproximado de la desaparición.
- Circunstancias del hecho (por ejemplo: “salió de su domicilio y no regresó”, “fue vista por última vez en…”, etc.).

### Recomendaciones

- Utilizar una fotografía clara, reciente y en la que la persona pueda reconocerse fácilmente.
- Describir características distintivas (cicatrices, tatuajes, marcas de nacimiento).
- Indicar si la persona requiere medicación o presenta condiciones de salud relevantes.
- Mantener la información actualizada si existieran novedades (según funcionalidades futuras del sistema).

---

## Reportar un Avistamiento

### ¿Cuándo reportar un avistamiento?

Se recomienda reportar un avistamiento cuando:

- Se observa a una persona que coincide razonablemente con la descripción de alguien reportado como desaparecido.
- Existen dudas, pero la similitud es significativa (en caso de duda, es preferible reportar).
- Se cuenta con información reciente sobre el posible paradero.

### Requisitos previos

- Estar registrado como avistador.
- Tener la sesión iniciada en el sistema.

### Pasos generales

1. Acceder a la opción **“Reportar avistamiento”** desde el menú o desde el perfil de una persona desaparecida.
2. Seleccionar la persona desaparecida asociada:
    - Búsqueda por DNI, nombre o apellido.
3. Indicar la **ubicación** del avistamiento:
    - Permitir que el sistema tome la ubicación GPS actual (si el navegador lo solicita).
    - O bien, seleccionar manualmente el punto en el mapa.
4. Completar:
    - Fecha y hora del avistamiento.
    - Descripción de lo observado.
5. Adjuntar una fotografía (opcional).
6. Confirmar el envío.

![Modal de Reporte de Avistamiento](img/reporte_avistamiento.png)

### Datos del avistamiento

**Obligatorios:**

- Persona desaparecida asociada.
- Coordenadas o ubicación seleccionada en el mapa.
- Fecha y hora del avistamiento.
- Descripción detallada de la situación.

**Opcionales pero recomendados:**

- Fotografía del entorno o de la persona vista (si la situación lo permite).
- Descripción de la vestimenta observada.
- Estado aparente (por ejemplo: solo, acompañado, tranquilo, desorientado).
- Dirección en la que se desplazaba.
- Medio de transporte (a pie, vehículo particular, transporte público, etc.).

### Geolocalización

El sistema puede capturar automáticamente la ubicación geográfica si:

- El dispositivo tiene el GPS activado.
- Se otorgan permisos de ubicación al navegador cuando los solicita.

Alternativamente, el usuario puede:

- Seleccionar un punto en el mapa haciendo clic en la ubicación.
- Ingresar una dirección aproximada para ubicar la zona.

---

## Buscar Personas Desaparecidas

### Métodos de búsqueda disponibles

#### Búsqueda por DNI (exacta)

1. Acceder a la sección **“Buscar personas”**.
2. Ingresar el DNI completo en el campo de búsqueda.
3. Confirmar la búsqueda.
4. Si existe un registro para dicho DNI, se mostrará el perfil correspondiente.

![Búsqueda por DNI](img/busqueda_dni.png)

#### Búsqueda por nombre

1. Ingresar el nombre completo o parcial.
2. Opcionalmente, combinar con apellido para acotar resultados.
3. El sistema mostrará una lista de personas cuyo nombre coincida o sea similar.

#### Búsqueda por apellido

1. Ingresar el apellido completo o parcial.
2. Opcionalmente, combinar con nombre para mayor precisión.
3. El sistema listará las coincidencias.

### Resultados de búsqueda

Cada resultado muestra, de forma resumida:

- Fotografía (si está disponible).
- Nombre completo.
- DNI.
- Edad.
- Fecha del primer reporte.
- Cantidad de avistamientos registrados.
- Estado del caso (por ejemplo: “activo” / “encontrado” / “cerrado”).

Desde esta vista se puede acceder al **perfil detallado** de la persona.

---

## Uso del Mapa Interactivo

### Funcionalidades principales

**Visualización:**

- Marcadores en el mapa que representan avistamientos públicos.
- Notificaciones y actualización en tiempo real: los nuevos avistamientos aparecen instantáneamente en tu pantalla gracias a tecnología de WebSockets sin necesidad de recargar.
- Colores diferenciados para identificar a cada persona desaparecida.
- Posible diferenciación visual entre casos recientes y más antiguos.

**Interacción:**

- Acercar o alejar el mapa con la rueda del mouse o con los botones **+ / −**.
- Desplazar el mapa arrastrando con el cursor o el dedo (en dispositivos táctiles).
- Hacer clic en un marcador para ver el detalle del avistamiento.

![Mapa Interactivo con Filtros](img/mapa_filtros.png)

**Filtros (según implementación):**

- Filtrar por rango de fechas.
- Filtrar por persona específica.
- Trazar ruta: al filtrar por una persona específica, se dibujará una línea conectando todos sus avistamientos en orden cronológico (requiere que tenga al menos 2 avistamientos), enumerando los marcadores dinámicamente.
- Última Ubicación: de la persona seleccionada, expone únicamente el reporte vital más reciente, optimizando el rendimiento usando el marcador "1".
- Filtrar solo casos activos o incluir casos cerrados.

### Código de colores (ejemplo de referencia)

| Color / Estilo        | Significado                                                  |
|-----------------------|-------------------------------------------------------------|
| Color por persona     | Cada persona desaparecida tiene un color asignado           |
| Marcador “verificado” | Avistamientos con validación adicional resaltados           |
| Casos recientes       | Color más intenso o marcador destacado                      |
| Casos antiguos        | Color atenuado                                              |

---

## Visualización de Perfiles de Personas

### Información disponible en el perfil

Cada perfil de persona desaparecida incluye:

**Datos personales:**

- Fotografía (si está disponible).
- Nombre completo.
- DNI.
- Edad aproximada.
- Fecha de desaparición.

**Mapa individual:**

- Mapa con todos los avistamientos asociados a esa persona.
- Marcadores ordenados cronológicamente.

**Línea de tiempo (timeline) de avistamientos:**

- Listado de avistamientos ordenados por fecha (más reciente primero).
- Detalles de cada avistamiento:
    - Fecha y hora.
    - Ubicación aproximada.
    - Descripción del reporte.
    - Datos básicos del avistador (por ejemplo, nombre).

### Interacción desde el perfil

- Al seleccionar un avistamiento en la línea de tiempo, el mapa se centra en la ubicación correspondiente.
- Es posible alternar entre distintos avistamientos para observar patrones o recorridos.

---

## Preguntas Frecuentes

### Generales

**¿Es necesario crear una cuenta para ver el mapa?**  
No. El mapa con avistamientos públicos puede consultarse sin iniciar sesión. Solo se requiere una cuenta para reportar personas desaparecidas o registrar avistamientos.

**¿Los reportes son anónimos?**  
No. Para realizar reportes es obligatorio registrarse como avistador y validar la identidad mediante DNI. Esto busca garantizar la seriedad del sistema y reducir reportes falsos.

**¿Puedo editar un avistamiento luego de enviarlo?**  
En la versión actual no se permite la edición de avistamientos una vez registrados. Si se comete un error, se recomienda contactar al equipo de soporte para solicitar la corrección.

**¿Qué sucede con mis datos personales?**  
Los datos se tratan conforme a la legislación vigente en materia de protección de datos personales (Ley 25.326 en Argentina). Puede consultarse la [Política de Privacidad](#política-de-privacidad) para más detalles.

### Sobre reportes

**¿Puedo reportar avistamientos ocurridos días o semanas atrás?**  
Sí. Es posible registrar avistamientos retrospectivos, aunque el sistema prioriza la información reciente para la toma de decisiones.

**¿Qué pasa si alguien reporta información falsa?**  
Los reportes malintencionados pueden derivar en la suspensión o bloqueo de la cuenta y, eventualmente, en acciones legales por parte de las autoridades competentes.

**¿Puedo adjuntar más de una fotografía en un avistamiento?**  
En la versión actual se permite adjuntar una fotografía por avistamiento. Futuras versiones podrían ampliar esta funcionalidad.

### Aspectos técnicos

**¿Por qué no funciona la localización GPS?**  
Se recomienda verificar:
- Que la geolocalización esté activada en el dispositivo.
- Que el navegador tenga permisos de ubicación habilitados para el sitio de Find Me.
- Actualizar la página e intentar nuevamente.

**¿El sistema funciona en dispositivos móviles?**  
Sí. La interfaz está diseñada para adaptarse a distintos tamaños de pantalla y puede utilizarse en navegadores móviles modernos.

**¿Es necesario instalar una aplicación?**  
No. Find Me es una aplicación web y se utiliza directamente desde el navegador, sin necesidad de instalación.

---

## Soporte y Contacto

Dado que se trata de un proyecto académico, los canales de contacto aquí indicados son de carácter ilustrativo.

### Reporte de problemas técnicos

En caso de detectar errores o problemas técnicos, se recomienda:

1. Registrar los pasos que llevaron al error.
2. Tomar una captura de pantalla si es posible.
3. Enviar un correo electrónico a:  
   `soporte@findme-ejemplo.com` (correo de referencia en el marco del proyecto).

### Sugerencias y mejoras

Para sugerir nuevas funcionalidades o mejoras:

- Enviar un correo electrónico a:  
  `sugerencias@findme-ejemplo.com`
- O bien utilizar el formulario de contacto disponible en la aplicación (si está habilitado).

### Emergencias reales

**Find Me no reemplaza los canales oficiales de emergencia.**

Ante una situación real de emergencia se debe contactar a:

- **Línea 142:** Menores extraviados (servicio gratuito, 24/7).
- **911:** Emergencias generales.
- **Sistema Federal de Búsqueda de Personas (SIFEBU).**
- **Organizaciones especializadas** como Missing Children Argentina.

---

## Términos y Condiciones

Al utilizar Find Me, el usuario acepta:

- Proporcionar información veraz y precisa.
- No utilizar el sistema para fines ilícitos, difamatorios o ajenos al objeto del proyecto.
- Comprender que Find Me es una herramienta complementaria y no sustituye la actuación de autoridades competentes.
- Respetar la privacidad y dignidad de las personas involucradas.

La versión completa de los términos y condiciones se encuentra en el documento **“Términos y Condiciones de Uso - Find Me”**, anexado a la documentación del proyecto.

---

## Política de Privacidad

Find Me se compromete a proteger los datos personales de los usuarios conforme a la Ley 25.326 de Protección de Datos Personales y normativa asociada.

En términos generales:

- Los datos se utilizan exclusivamente para los fines del sistema (gestión de casos, avistamientos y contacto).
- No se comercializan datos personales.
- El usuario puede solicitar la rectificación o eliminación de sus datos conforme a la normativa vigente.

La política completa se detalla en el documento **“Política de Privacidad - Find Me”**, anexado a la documentación del proyecto.

---

**Última actualización:** Diciembre 2025  
**Versión:** 1.0 (manual de usuario — versión inicial)

*En el marco del trabajo final, se prevé ampliar este manual con capturas de pantalla, ejemplos paso a paso y material audiovisual en versiones futuras del sistema.*
