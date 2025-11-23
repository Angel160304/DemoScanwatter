const CACHE_NAME = "ScanWatter-v6"; // SUBE ESTA VERSIÓN

const urlsToCache = [
  "/dashboard.html",
  "/index.html",
  "/login.html",
  "/registro.html",
  "/css/style.css",
  "/css/style2.css",
  "/css/style3.css",
  "/css/style4.css",
  "/js/script.js",
  "/js/auth.js",
  "/img/logo_blanco.png"
];

// INSTALACIÓN
self.addEventListener("install", event => {
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(urlsToCache))
  );
});

// ACTIVACIÓN
self.addEventListener("activate", event => {
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(keys.map(key => key !== CACHE_NAME && caches.delete(key)))
    )
  );
  self.clients.claim();
});

// FETCH
self.addEventListener("fetch", event => {
  if (event.request.url.includes("/api/")) return;

  event.respondWith(
    caches.match(event.request).then(response =>
      response || fetch(event.request).catch(() => caches.match("/index.html"))
    )
  );
});
