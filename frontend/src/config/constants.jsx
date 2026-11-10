// === Endpoints de la API ===
export const API_ENDPOINTS = {
  DESAPARECIDOS: "/api/v1/desaparecidos",
  AVISTADORES: "/api/v1/avistadores",
  AUTH: "/api/v1/auth",
  AVISTAMIENTOS: "/api/v1/avistamientos",
  FILES: "/api/v1/files"
};

// === Mapeo de errores ===
export const ERROR_MAPS = {
  AVISTADOR: {
    400: {
      "validation.failed": "Datos inválidos (revisá el formulario).",
      "request.body.invalid": "Los datos enviados tienen formato incorrecto.",
      "request.param.missing": "Falta un dato requerido.",
      "request.param.type_mismatch": "Un dato tiene formato incorrecto.",
      default: "Datos inválidos (revisá el formulario)."
    },
    404: { default: "No existe en padrón (RENAPER)." },
    409: { default: "DNI ya registrado." },
    422: {
      "avistador.renaper.nomatch": "Los datos no coinciden con el padrón.",
      "avistador.edad.menor": "Debés ser mayor de edad para registrarte.",
      default: "Datos inválidos."
    },
    500: {
      "server.internal_error": "Error interno del servidor. Intentá de nuevo.",
      default: "Error interno del servidor."
    }
  },

  DESAPARECIDO: {
    400: {
      "validation.failed": "Datos inválidos (revisá el formulario).",
      "request.body.invalid": "Los datos enviados tienen formato incorrecto.",
      "request.param.missing": "Falta un dato requerido.",
      "request.param.type_mismatch": "Un dato tiene formato incorrecto.",
      default: "Datos inválidos."
    },
    401: { default: "Debés iniciar sesión para registrar un desaparecido." },
    409: { default: "Ya existe un desaparecido con ese DNI." },
    404: {
      "desaparecido.renaper.notfound": "No se encontró el DNI en el padrón de RENAPER.",
      default: "Recurso no encontrado."
    },
    422: {
      "desaparecido.descripcion.corta": "La descripción debe tener al menos 20 caracteres.",
      default: "Los datos enviados no son válidos."
    },
    500: {
      "server.internal_error": "Error interno del servidor. Intentá de nuevo.",
      default: "Error interno del servidor."
    }
  },

  AUTH: {
    400: {
      "validation.failed": "Datos inválidos.",
      "request.body.invalid": "Los datos enviados tienen formato incorrecto.",
      default: "Datos inválidos."
    },
    404: {
      "auth.login.renaper.notfound": "No existe en padrón (RENAPER).",
      "auth.login.user.notfound": "Usuario no registrado. Primero debés registrarte.",
      default: "Usuario no encontrado.",
    },
    422: {
      "auth.login.email.mismatch": "El email no coincide con el registrado.",
      default: "Datos inválidos.",
    },
    401: {
      "auth.token.notfound": "No encontramos tu sesión. Iniciá sesión de nuevo.",
      "auth.token.expired": "Tu sesión expiró. Volvé a iniciar sesión.",
      "auth.token.invalid": "Token inválido. Probá iniciar sesión otra vez.",
      "auth.token.malformed": "Token mal formado.",
      "auth.token.data.missing": "Faltan datos en el token.",
      default: "Credenciales inválidas.",
    },
    500: {
      "server.internal_error": "Error interno del servidor. Intentá de nuevo.",
      default: "Error interno del servidor."
    }
  },

  AVISTAMIENTO: {
    400: {
      "validation.failed": "Datos inválidos (revisá el formulario).",
      "request.body.invalid": "Los datos enviados tienen formato incorrecto.",
      "request.param.missing": "Falta un dato requerido.",
      "request.param.type_mismatch": "Un dato tiene formato incorrecto.",
      default: "Datos inválidos."
    },
    404: {
      "avistamiento.avistador.notfound": "El avistador no existe.",
      "avistamiento.desaparecido.notfound": "El desaparecido no existe.",
      default: "Recurso no encontrado."
    },
    422: {
      "avistamiento.coords.invalidas": "Las coordenadas son inválidas.",
      "avistamiento.coords.lat.required": "La latitud es obligatoria.",
      "avistamiento.coords.lat.range": "La latitud está fuera de rango.",
      "avistamiento.coords.lat.naninf": "La latitud no es un número válido.",
      "avistamiento.coords.lng.required": "La longitud es obligatoria.",
      "avistamiento.coords.lng.range": "La longitud está fuera de rango.",
      "avistamiento.coords.lng.naninf": "La longitud no es un número válido.",
      "avistamiento.descripcion.corta": "La descripción debe tener al menos 20 caracteres.",
      "avistamiento.poligono.wkt.required": "Debes enviar un poligono valido para esta busqueda.",
      "avistamiento.poligono.wkt.invalid": "El formato del poligono es invalido.",
      "avistamiento.poligono.wkt.too_large": "El poligono enviado es demasiado grande.",
      default: "Datos inválidos."
    },
    401: { default: "Debés iniciar sesión para reportar un avistamiento." },
    500: {
      "server.internal_error": "Error interno del servidor. Intentá de nuevo.",
      default: "Error interno del servidor."
    }
  },

  FILE: {
    400: {
      "file.empty": "No se seleccionó ningún archivo.",
      "file.too_large": "El archivo supera el tamaño máximo permitido.",
      "file.no_extension": "El archivo no tiene extensión.",
      "file.type_not_allowed": "Tipo de archivo no permitido (solo JPG, PNG, WEBP, PDF).",
      "file.mime_mismatch": "El tipo del archivo no coincide con su extensión.",
      "file.magic_bytes_mismatch": "El contenido del archivo no coincide con su tipo declarado.",
      "file.corrupted": "El archivo parece estar corrupto.",
      "validation.failed": "Datos del archivo inválidos.",
      "request.param.missing": "Falta un parámetro del archivo.",
      "request.param.type_mismatch": "Un parámetro tiene formato incorrecto.",
      default: "Error en el archivo enviado."
    },
    403: {
      "file.path_traversal": "Ruta de archivo no permitida.",
      default: "No tenés permiso para esta operación."
    },
    404: {
      "file.not_found": "Archivo no encontrado.",
      default: "Archivo no encontrado."
    },
    500: {
      "file.storage_error": "Error interno al guardar el archivo. Intentá de nuevo.",
      "server.internal_error": "Error interno del servidor. Intentá de nuevo.",
      default: "Error interno del servidor."
    }
  }
};