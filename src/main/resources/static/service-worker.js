const CACHE_NAME = "ScanWatter-v5"; // ⬅ CAMBIA EL NÚMERO CADA VEZ QUE HAGAS CAMBIOS

const urlsToCache = [
  //"dashboard.html?v=5",
  "/index.html?v=5",
  "/login.html?v=5",
  "/registro.html?v=5",
  "/css/style.css?v=5",
  "/css/style2.css?v=5",
  "/css/style3.css?v=5",
  "/js/script.js?v=5",
  "/js/auth.js?v=5",
  "/img/logo_blanco.png?v=5"
];

// INSTALACIÓN
self.addEventListener("install", event => {
  self.skipWaiting(); // 📌 Fuerza actualización inmediata
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      return Promise.all(
        urlsToCache.map(url =>
          cache.add(url).catch(err =>
            console.warn("No se pudo cachear:", url, err)
          )
        )
      );
    })
  );
});

// ACTIVACIÓN – limpia caché vieja
self.addEventListener("activate", event => {
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(
        keys.map(key => {
          if (key !== CACHE_NAME) return caches.delete(key);
        })
      )
    )
  );
  self.clients.claim(); // 📌 Lo activa sin esperar reinicio
});

// FETCH
self.addEventListener("fetch", event => {
  // Deja pasar llamadas al backend
  if (event.request.url.includes("/api/")) return;

  event.respondWith(
    caches.match(event.request).then(response => response || fetch(event.request))
  );
});
