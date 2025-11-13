const CACHE_NAME = "ScanWatter";
const urlsToCache = [
  "/index.html",
  "/login.html",
  "/registro.html",
  "/css/style.css",
  "/css/style2.css",
  "/css/style3.css",
  "/js/script.js",
  "/js/auth.js",
  "/img/logo_blanco.png"
];

self.addEventListener("install", event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      return Promise.all(
        urlsToCache.map(url =>
          cache.add(url).catch(err => {
            console.warn("No se pudo cachear:", url, err);
          })
        )
      );
    })
  );
});


// Activar SW y limpiar los  viejos caches
self.addEventListener("activate", event => {
  event.waitUntil(
    caches.keys().then(keys => {
      return Promise.all(
        keys.filter(key => key !== CACHE_NAME).map(key => caches.delete(key))
      );
    })
  );
});

// Interceptar solicitudes y servir desde caché si es posible
self.addEventListener("fetch", event => {
  event.respondWith(
    caches.match(event.request).then(response => {
      return response || fetch(event.request);
    })
  );
});
