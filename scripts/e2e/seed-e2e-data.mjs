const backendUrl = process.env.BACKEND_URL ?? 'http://localhost:8080';

function requiredEnv(name) {
  const value = process.env[name];
  if (!value) throw new Error(`Missing env var: ${name}`);
  return value;
}

const user = {
  dni: requiredEnv('E2E_USER_DNI'),
  email: requiredEnv('E2E_USER_EMAIL'),
  nombre: process.env.E2E_USER_NOMBRE ?? 'Juan',
  apellido: process.env.E2E_USER_APELLIDO ?? 'Perez',
  edad: Number(process.env.E2E_USER_EDAD ?? 30),
  direccion: process.env.E2E_USER_DIRECCION ?? 'Calle E2E 123',
  telefono: process.env.E2E_USER_TELEFONO ?? '1111111111',
};

const desaparecido = {
  dni: requiredEnv('E2E_MISSING_DNI'),
  nombre: process.env.E2E_MISSING_NOMBRE ?? 'Carlos',
  apellido: process.env.E2E_MISSING_APELLIDO ?? 'Vazquez',
  edad: Number(process.env.E2E_MISSING_EDAD ?? 35),
  descripcion:
    process.env.E2E_MISSING_DESC ??
    'Persona vista por ultima vez cerca del centro con campera azul.',
  contactoFamiliar:
    process.env.E2E_MISSING_CONTACTO ?? 'familia.e2e@findme.test',
  ultimaUbicacion: process.env.E2E_MISSING_UBICACION ?? 'Cerca del obelisco',
  fechaDesaparicion:
    process.env.E2E_MISSING_FECHA ??
    new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
};

const paginationSeed = {
  count: Number(process.env.E2E_PAGINATION_COUNT ?? 15),
  dniStart: Number(process.env.E2E_PAGINATION_DNI_START ?? 36000001),
};

async function request(path, options = {}) {
  const response = await fetch(`${backendUrl}${path}`, options);
  return response;
}

async function seedAvistador() {
  const response = await request('/api/v1/avistadores', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user),
  });

  if (![201, 409].includes(response.status)) {
    const body = await response.text();
    throw new Error(`Error al crear avistador (${response.status}): ${body}`);
  }
}

async function loginAndGetCookie() {
  const response = await request('/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ dni: user.dni, email: user.email }),
  });

  if (response.status !== 200) {
    const body = await response.text();
    throw new Error(`Error de login seed (${response.status}): ${body}`);
  }

  const setCookie = response.headers.get('set-cookie');
  if (!setCookie) {
    throw new Error('Login seed sin Set-Cookie');
  }

  return setCookie.split(';')[0];
}

async function seedDesaparecido(cookie, payload) {
  const response = await request('/api/v1/desaparecidos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Cookie: cookie,
    },
    body: JSON.stringify(payload),
  });

  if (![201, 409].includes(response.status)) {
    const body = await response.text();
    throw new Error(`Error al crear desaparecido (${response.status}): ${body}`);
  }
}

async function seedPaginationDataset(cookie) {
  const jobs = [];

  for (let i = 0; i < paginationSeed.count; i += 1) {
    const dni = String(paginationSeed.dniStart + i);
    jobs.push(
      seedDesaparecido(cookie, {
        nombre: `PaginacionE2E ${String(i + 1).padStart(2, '0')}`,
        apellido: 'Seed',
        edad: 30,
        dni,
        descripcion: `Caso semilla E2E para paginacion numero ${i + 1} con descripcion larga de prueba.`,
        contactoFamiliar: `pagination+${i + 1}@findme.test`,
        ultimaUbicacion: `Zona Seed ${i + 1}, CABA`,
        fechaDesaparicion: new Date(Date.now() - (i + 2) * 3600 * 1000).toISOString(),
      })
    );
  }

  for (const job of jobs) {
    await job;
  }
}

(async () => {
  await seedAvistador();
  const cookie = await loginAndGetCookie();

  await seedDesaparecido(cookie, {
    nombre: desaparecido.nombre,
    apellido: desaparecido.apellido,
    edad: desaparecido.edad,
    dni: desaparecido.dni,
    descripcion: desaparecido.descripcion,
    contactoFamiliar: desaparecido.contactoFamiliar,
    ultimaUbicacion: desaparecido.ultimaUbicacion,
    fechaDesaparicion: desaparecido.fechaDesaparicion,
  });

  await seedPaginationDataset(cookie);
  console.log('E2E seed listo (base + paginacion)');
})();
