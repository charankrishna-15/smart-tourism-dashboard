/* ═══════════════════════════════════════════════════════════════
   SMART TOURISM SYSTEM — Frontend Data & Chart Engine
   Simulates all 5 Java modules in the browser with real data
═══════════════════════════════════════════════════════════════ */

"use strict";

// ── CHART.js default style ──────────────────────────────────────
Chart.defaults.color = '#9899b3';
Chart.defaults.font.family = "'Inter', sans-serif";
Chart.defaults.font.size = 11;
Chart.defaults.plugins.legend.labels.boxWidth = 12;
Chart.defaults.plugins.legend.labels.padding = 14;

// ── PALETTE ─────────────────────────────────────────────────────
const C = {
  purple: '#7c6ef7', pink: '#f06a9b', teal: '#00d2b4',
  amber: '#f9ae3b', blue: '#4fa3e8', red: '#ff6b6b',
  green: '#55efc4', orange: '#e17055',
};
function alpha(hex, a) {
  const r = parseInt(hex.slice(1, 3), 16), g = parseInt(hex.slice(3, 5), 16), b = parseInt(hex.slice(5, 7), 16);
  return `rgba(${r},${g},${b},${a})`;
}

// ── DESTINATION DATA (mirrors Java DB) ──────────────────────────
const DESTINATIONS = [
  { id: 1, name: 'Taj Mahal, Agra', country: 'India', climate: 'temperate', style: 'cultural', rating: 4.9, reviews: 15000, cost: 3600, seasonStart: 10, seasonEnd: 3, lat: 27.1751, lon: 78.0421, emoji: '🕌', activities: 'Sightseeing, Photography, History Tours' },
  { id: 2, name: 'Goa', country: 'India', climate: 'tropical', style: 'relaxation', rating: 4.7, reviews: 20000, cost: 7200, seasonStart: 11, seasonEnd: 2, lat: 15.2993, lon: 74.1240, emoji: '🏖️', activities: 'Beaches, Water Sports, Nightlife' },
  { id: 3, name: 'Jaipur', country: 'India', climate: 'arid', style: 'cultural', rating: 4.8, reviews: 18000, cost: 5400, seasonStart: 10, seasonEnd: 3, lat: 26.9124, lon: 75.7873, emoji: '🏰', activities: 'Forts, Palaces, Shopping' },
  { id: 4, name: 'Kerala Backwaters', country: 'India', climate: 'tropical', style: 'relaxation', rating: 4.8, reviews: 25000, cost: 5400, seasonStart: 9, seasonEnd: 3, lat: 9.4981, lon: 76.3388, emoji: '🌴', activities: 'Houseboats, Ayurveda, Nature Walks' },
  { id: 5, name: 'Leh Ladakh', country: 'India', climate: 'arctic', style: 'adventure', rating: 4.9, reviews: 30000, cost: 9000, seasonStart: 5, seasonEnd: 9, lat: 34.1526, lon: 77.5771, emoji: '🏔️', activities: 'Trekking, Biking, Monasteries' },
  { id: 6, name: 'Rishikesh', country: 'India', climate: 'temperate', style: 'adventure', rating: 4.6, reviews: 12000, cost: 4500, seasonStart: 9, seasonEnd: 5, lat: 30.0869, lon: 78.2676, emoji: '🧘', activities: 'Yoga, River Rafting, Temples' },
  { id: 7, name: 'Varanasi', country: 'India', climate: 'temperate', style: 'cultural', rating: 4.7, reviews: 10000, cost: 3600, seasonStart: 10, seasonEnd: 3, lat: 25.3176, lon: 82.9739, emoji: '🪔', activities: 'Ghats, Temples, Boat Ride' },
  { id: 8, name: 'Darjeeling', country: 'India', climate: 'temperate', style: 'relaxation', rating: 4.6, reviews: 18000, cost: 5400, seasonStart: 3, seasonEnd: 5, lat: 27.0360, lon: 88.2627, emoji: '☕', activities: 'Tea Gardens, Toy Train, Mountain Views' },
  { id: 9, name: 'Andaman Islands', country: 'India', climate: 'tropical', style: 'relaxation', rating: 4.8, reviews: 40000, cost: 9000, seasonStart: 10, seasonEnd: 5, lat: 11.7401, lon: 92.6586, emoji: '🏝️', activities: 'Scuba Diving, Snorkeling, Beaches' },
  { id: 10, name: 'Hampi', country: 'India', climate: 'arid', style: 'cultural', rating: 4.7, reviews: 15000, cost: 4500, seasonStart: 10, seasonEnd: 3, lat: 15.3350, lon: 76.4600, emoji: '🏛️', activities: 'Temples, Ruins, Bouldering' },
  { id: 11, name: 'Munnar', country: 'India', climate: 'tropical', style: 'relaxation', rating: 4.7, reviews: 20000, cost: 5400, seasonStart: 9, seasonEnd: 5, lat: 10.0889, lon: 77.0595, emoji: '🍃', activities: 'Tea Gardens, Trekking, Waterfalls' },
  { id: 12, name: 'Udaipur', country: 'India', climate: 'arid', style: 'cultural', rating: 4.8, reviews: 22000, cost: 5400, seasonStart: 10, seasonEnd: 3, lat: 24.5854, lon: 73.7125, emoji: '🚤', activities: 'Palaces, Boat Ride, Lakes' },
  { id: 13, name: 'Manali', country: 'India', climate: 'temperate', style: 'adventure', rating: 4.7, reviews: 18000, cost: 7200, seasonStart: 3, seasonEnd: 6, lat: 32.2396, lon: 77.1887, emoji: '🎿', activities: 'Skiing, Trekking, Temples' },
  { id: 14, name: 'Ranthambore', country: 'India', climate: 'arid', style: 'adventure', rating: 4.6, reviews: 25000, cost: 4500, seasonStart: 10, seasonEnd: 4, lat: 26.0173, lon: 76.2253, emoji: '🐅', activities: 'Wildlife Safari, Tiger Spotting' },
  { id: 15, name: 'Mysore', country: 'India', climate: 'tropical', style: 'cultural', rating: 4.6, reviews: 12000, cost: 3600, seasonStart: 10, seasonEnd: 2, lat: 12.2958, lon: 76.6394, emoji: '👑', activities: 'Palaces, Silk Weaving, Gardens' },
  { id: 16, name: 'Srinagar', country: 'India', climate: 'temperate', style: 'relaxation', rating: 4.8, reviews: 25000, cost: 7200, seasonStart: 4, seasonEnd: 10, lat: 34.0837, lon: 74.7973, emoji: '🌺', activities: 'Shikara Ride, Gardens, Houseboat' },
  { id: 17, name: 'Kanyakumari', country: 'India', climate: 'tropical', style: 'cultural', rating: 4.5, reviews: 12000, cost: 3600, seasonStart: 10, seasonEnd: 3, lat: 8.0883, lon: 77.5385, emoji: '🌅', activities: 'Vivekananda Rock, Sunset, Temples' },
  { id: 18, name: 'Kaziranga', country: 'India', climate: 'tropical', style: 'adventure', rating: 4.7, reviews: 20000, cost: 5400, seasonStart: 11, seasonEnd: 4, lat: 26.5775, lon: 93.1711, emoji: '🦏', activities: 'Wildlife Safari, Elephant Ride' },
  { id: 19, name: 'Pushkar', country: 'India', climate: 'arid', style: 'cultural', rating: 4.5, reviews: 10000, cost: 3600, seasonStart: 10, seasonEnd: 3, lat: 26.4905, lon: 74.5504, emoji: '🐪', activities: 'Temple, Lake, Camel Ride' },
  { id: 20, name: 'Sundarbans', country: 'India', climate: 'tropical', style: 'adventure', rating: 4.6, reviews: 15000, cost: 5400, seasonStart: 10, seasonEnd: 3, lat: 21.9497, lon: 89.1833, emoji: '🚤', activities: 'Boat Safari, Mangrove Forest' },
  { id: 21, name: 'Ooty', country: 'India', climate: 'temperate', style: 'relaxation', rating: 4.6, reviews: 14000, cost: 4500, seasonStart: 3, seasonEnd: 6, lat: 11.4064, lon: 76.6932, emoji: '🚂', activities: 'Botanical Gardens, Toy Train, Lake' },
  { id: 22, name: 'Shimla', country: 'India', climate: 'temperate', style: 'relaxation', rating: 4.7, reviews: 22000, cost: 5400, seasonStart: 3, seasonEnd: 6, lat: 31.1048, lon: 77.1734, emoji: '🌲', activities: 'Mall Road, Ridge, Toy Train' },
  { id: 23, name: 'Jaisalmer', country: 'India', climate: 'arid', style: 'adventure', rating: 4.8, reviews: 16000, cost: 6000, seasonStart: 10, seasonEnd: 3, lat: 26.9157, lon: 70.9083, emoji: '⛺', activities: 'Desert Safari, Fort, Camping' },
  { id: 24, name: 'Amritsar', country: 'India', climate: 'temperate', style: 'cultural', rating: 4.9, reviews: 25000, cost: 3600, seasonStart: 10, seasonEnd: 3, lat: 31.6340, lon: 74.8723, emoji: '🕌', activities: 'Golden Temple, Wagah Border, Food' },
  { id: 25, name: 'Pondicherry', country: 'India', climate: 'tropical', style: 'relaxation', rating: 4.6, reviews: 18000, cost: 4800, seasonStart: 10, seasonEnd: 3, lat: 11.9416, lon: 79.8083, emoji: '🥖', activities: 'French Quarter, Beaches, Auroville' },
  { id: 26, name: 'Mahabaleshwar', country: 'India', climate: 'temperate', style: 'relaxation', rating: 4.5, reviews: 12000, cost: 4500, seasonStart: 10, seasonEnd: 5, lat: 17.9307, lon: 73.6477, emoji: '🍓', activities: 'Viewpoints, Strawberries, Lake' },
  { id: 27, name: 'Coorg', country: 'India', climate: 'tropical', style: 'relaxation', rating: 4.7, reviews: 19000, cost: 5400, seasonStart: 10, seasonEnd: 4, lat: 12.3375, lon: 75.8069, emoji: '☕', activities: 'Coffee Plantations, Waterfalls, Trekking' },
  { id: 28, name: 'Spiti Valley', country: 'India', climate: 'arctic', style: 'adventure', rating: 4.8, reviews: 10000, cost: 7500, seasonStart: 5, seasonEnd: 10, lat: 32.2396, lon: 78.0349, emoji: '🏍️', activities: 'Road Trip, Monasteries, Camping' },
  { id: 29, name: 'Auli', country: 'India', climate: 'arctic', style: 'adventure', rating: 4.7, reviews: 8000, cost: 6600, seasonStart: 12, seasonEnd: 3, lat: 30.5333, lon: 79.5667, emoji: '⛷️', activities: 'Skiing, Cable Car, Trekking' },
  { id: 30, name: 'Khajuraho', country: 'India', climate: 'arid', style: 'cultural', rating: 4.8, reviews: 11000, cost: 4200, seasonStart: 10, seasonEnd: 3, lat: 24.8318, lon: 79.9199, emoji: '🛕', activities: 'Temples, Heritage walks, Light Show' }
];

const TOURISTS = [
  { name: 'Charan', climate: 'tropical', style: 'relaxation', budget: 100000, days: 14 },
  { name: 'Jahnavi', climate: 'temperate', style: 'adventure', budget: 240000, days: 21 },
  { name: 'Deekshitha', climate: 'arid', style: 'cultural', budget: 150000, days: 10 },
  { name: 'Anil', climate: 'tropical', style: 'adventure', budget: 180000, days: 18 },
  { name: 'Dinesh', climate: 'temperate', style: 'cultural', budget: 270000, days: 28 },
  { name: 'Irshad', climate: 'arctic', style: 'adventure', budget: 225000, days: 14 },
  { name: 'Harsha', climate: 'tropical', style: 'cultural', budget: 120000, days: 12 },
  { name: 'Shankar', climate: 'temperate', style: 'relaxation', budget: 195000, days: 15 },
  { name: 'Abhinay', climate: 'arid', style: 'adventure', budget: 165000, days: 20 },
  { name: 'Rafi', climate: 'arctic', style: 'cultural', budget: 255000, days: 25 },
];

// Seasonal data (3 years × 12 months) for Taj Mahal, Goa, Leh Ladakh
const SEASONAL = {
  taj: {
    name: 'Taj Mahal, Agra', id: 1, trend: '📈 Growing',
    historical: [18000, 16000, 22000, 15000, 10000, 8000, 12000, 15000, 18000, 35000, 45000, 50000],
    forecast: [21000, 19000, 26000, 17000, 12000, 10000, 14000, 17000, 21000, 39000, 49000, 54000],
  },
  goa: {
    name: 'Goa', id: 2, trend: '📈 Growing',
    historical: [40000, 35000, 25000, 15000, 10000, 8000, 5000, 6000, 12000, 25000, 45000, 55000],
    forecast: [45000, 40000, 30000, 19000, 14000, 10000, 7000, 8000, 16000, 30000, 50000, 60000],
  },
  ladakh: {
    name: 'Leh Ladakh', id: 5, trend: '➡ Stable',
    historical: [1000, 1500, 3000, 8000, 18000, 30000, 35000, 30000, 20000, 10000, 3000, 1500],
    forecast: [1500, 2000, 4000, 10000, 22000, 35000, 42000, 35000, 25000, 14000, 4000, 2000],
  },
};

// ── UTILITY ─────────────────────────────────────────────────────
const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

function stars(r) { return '★'.repeat(Math.round(r)) + '☆'.repeat(5 - Math.round(r)); }

function haversine(lat1, lon1, lat2, lon2) {
  const R = 6371, dLat = (lat2 - lat1) * Math.PI / 180, dLon = (lon2 - lon1) * Math.PI / 180;
  const a = Math.sin(dLat / 2) ** 2 + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) ** 2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

function inSeason(d, month) {
  const m = parseInt(month);
  if (d.seasonStart <= d.seasonEnd) return m >= d.seasonStart && m <= d.seasonEnd;
  return m >= d.seasonStart || m <= d.seasonEnd;
}

function getCardColor(style) {
  return { adventure: C.teal, cultural: C.amber, relaxation: C.pink, eco: C.green }[style] || C.purple;
}

function getClimateEmoji(c) {
  return { tropical: '🌴', temperate: '🍃', arctic: '❄️', arid: '🏜️' }[c] || '🌍';
}

// ── CHART STORE ──────────────────────────────────────────────────
const charts = {};
function destroyChart(id) { if (charts[id]) { charts[id].destroy(); delete charts[id]; } }

function makeChart(id, config) {
  destroyChart(id);
  const ctx = document.getElementById(id);
  if (!ctx) return null;
  charts[id] = new Chart(ctx, config);
  return charts[id];
}

// ══════════════════════════════════════════════════════════════
// NAVIGATION
// ══════════════════════════════════════════════════════════════
const SECTION_LABELS = {
  dashboard: 'System Overview',
  destinations: 'Destination Database',
  recommend: 'Recommendation Engine',
  forecast: 'Forecast Engine',
  optimize: 'Itinerary Optimizer',
  reports: 'Analytics Reports',
};

function navigate(sectionId) {
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

  const sec = document.getElementById('section-' + sectionId);
  if (sec) sec.classList.add('active');

  const nav = document.getElementById('nav-' + sectionId);
  if (nav) nav.classList.add('active');

  document.getElementById('breadcrumb').textContent = SECTION_LABELS[sectionId] || sectionId;

  // Lazy init charts on first visit
  if (sectionId === 'dashboard' && !charts['ratingsChart']) initDashboardCharts();
  if (sectionId === 'forecast' && !charts['forecastChart']) initForecastCharts();
  if (sectionId === 'reports' && !charts['scatterChart']) initReportsCharts();

  // Close sidebar on mobile
  if (window.innerWidth < 768) document.getElementById('sidebar').classList.remove('open');
}

document.querySelectorAll('.nav-item').forEach(item => {
  item.addEventListener('click', e => {
    e.preventDefault();
    navigate(item.dataset.section);
  });
});

document.getElementById('menuToggle').addEventListener('click', () => {
  document.getElementById('sidebar').classList.toggle('open');
});

// ══════════════════════════════════════════════════════════════
// TOPBAR DATE
// ══════════════════════════════════════════════════════════════
document.getElementById('topbarDate').textContent =
  new Date().toLocaleDateString('en-IN', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' });

// ══════════════════════════════════════════════════════════════
// MODULE 1: DESTINATION DB — render cards
// ══════════════════════════════════════════════════════════════
function renderDestinations(dests) {
  const grid = document.getElementById('destGrid');
  grid.innerHTML = '';
  dests.forEach(d => {
    const card = document.createElement('div');
    card.className = 'dest-card';
    const cc = getCardColor(d.style);
    card.style.setProperty('--card-color', cc);
    card.innerHTML = `
      <div class="dest-flag">${d.emoji}</div>
      <div class="dest-name">${d.name}</div>
      <div class="dest-country">${getClimateEmoji(d.climate)} ${d.country}</div>
      <div class="dest-meta">
        <span class="dest-tag">${d.climate}</span>
        <span class="dest-tag">${d.style}</span>
      </div>
      <div class="dest-rating-row">
        <span class="dest-stars">${stars(d.rating)} ${d.rating}</span>
        <span class="dest-cost">₹${d.cost}/day</span>
      </div>
      <div style="font-size:0.7rem;color:#5a5b78;margin-top:8px">${d.activities}</div>
    `;
    grid.appendChild(card);
  });
}

function filterDestinations() {
  const q = document.getElementById('destSearch').value.toLowerCase();
  const climate = document.getElementById('destClimateFilter').value;
  const style = document.getElementById('destStyleFilter').value;
  const filtered = DESTINATIONS.filter(d => {
    const matchQ = !q || d.name.toLowerCase().includes(q) || d.country.toLowerCase().includes(q);
    const matchC = !climate || d.climate === climate;
    const matchS = !style || d.style === style;
    return matchQ && matchC && matchS;
  });
  renderDestinations(filtered);
}

document.getElementById('destSearch').addEventListener('input', filterDestinations);
document.getElementById('destClimateFilter').addEventListener('change', filterDestinations);
document.getElementById('destStyleFilter').addEventListener('change', filterDestinations);

// ══════════════════════════════════════════════════════════════
// DASHBOARD CHARTS
// ══════════════════════════════════════════════════════════════
function initDashboardCharts() {
  const sorted = [...DESTINATIONS].sort((a, b) => b.rating - a.rating);

  // Ratings bar chart
  makeChart('ratingsChart', {
    type: 'bar',
    data: {
      labels: sorted.slice(0, 8).map(d => d.name),
      datasets: [{
        label: 'Rating',
        data: sorted.slice(0, 8).map(d => d.rating),
        backgroundColor: sorted.slice(0, 8).map((_, i) =>
          alpha([C.purple, C.pink, C.teal, C.amber, C.blue, C.red, C.green, C.orange][i], 0.75)),
        borderColor: sorted.slice(0, 8).map((_, i) =>
          [C.purple, C.pink, C.teal, C.amber, C.blue, C.red, C.green, C.orange][i]),
        borderWidth: 2,
        borderRadius: 6,
      }],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { maxRotation: 35, font: { size: 10 } } },
        y: { grid: { color: 'rgba(255,255,255,0.04)' }, min: 4, max: 5.2, ticks: { stepSize: 0.2 } },
      },
    },
  });

  // Cluster scatter chart (simulated K-Means k=4)
  const clusters = [
    { label: 'Cluster A (Tropical/Relax)', color: C.pink, data: DESTINATIONS.filter(d => d.climate === 'tropical') },
    { label: 'Cluster B (Temperate/Cult)', color: C.amber, data: DESTINATIONS.filter(d => d.climate === 'temperate' && d.style === 'cultural') },
    { label: 'Cluster C (Temperate/Adv)', color: C.teal, data: DESTINATIONS.filter(d => d.climate === 'temperate' && d.style === 'adventure') },
    { label: 'Cluster D (Arid/Arctic)', color: C.purple, data: DESTINATIONS.filter(d => d.climate === 'arid' || d.climate === 'arctic') },
  ];
  makeChart('clusterChart', {
    type: 'scatter',
    data: {
      datasets: clusters.map(c => ({
        label: c.label,
        data: c.data.map(d => ({ x: d.cost, y: d.rating, label: d.name })),
        backgroundColor: alpha(c.color, 0.65),
        borderColor: c.color,
        pointRadius: 8,
        pointHoverRadius: 11,
      })),
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: {
        legend: { labels: { font: { size: 10 } } },
        tooltip: {
          callbacks: {
            label: ctx => `${ctx.raw.label}: ${ctx.raw.y}⭐ / ₹${ctx.raw.x}/day`,
          }
        },
      },
      scales: {
        x: { title: { display: true, text: 'Avg Cost/Day (INR)', font: { size: 10 } }, grid: { color: 'rgba(255,255,255,0.04)' } },
        y: { title: { display: true, text: 'Rating', font: { size: 10 } }, grid: { color: 'rgba(255,255,255,0.04)' }, min: 4, max: 5.2 },
      },
    },
  });

  // Style donut
  const styles = {};
  DESTINATIONS.forEach(d => styles[d.style] = (styles[d.style] || 0) + 1);
  makeChart('styleChart', {
    type: 'doughnut',
    data: {
      labels: Object.keys(styles).map(s => s.charAt(0).toUpperCase() + s.slice(1)),
      datasets: [{ data: Object.values(styles), backgroundColor: [C.teal, C.amber, C.pink, C.green], borderWidth: 0, hoverOffset: 8 }],
    },
    options: {
      responsive: true, maintainAspectRatio: false, cutout: '65%',
      plugins: { legend: { position: 'bottom' } },
    },
  });

  // Tourist budget scatter
  makeChart('budgetChart', {
    type: 'scatter',
    data: {
      datasets: [{
        label: 'Tourists',
        data: TOURISTS.map(t => ({ x: t.budget, y: t.days, label: t.name })),
        backgroundColor: [C.purple, C.pink, C.teal, C.amber, C.blue, C.red].map(c => alpha(c, 0.7)),
        pointRadius: 10, pointHoverRadius: 13,
        borderColor: [C.purple, C.pink, C.teal, C.amber, C.blue, C.red],
        borderWidth: 2,
      }],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: { callbacks: { label: ctx => `${ctx.raw.label}: ₹${ctx.raw.x} / ${ctx.raw.y} days` } },
      },
      scales: {
        x: { title: { display: true, text: 'Budget (INR)', font: { size: 10 } }, grid: { color: 'rgba(255,255,255,0.04)' } },
        y: { title: { display: true, text: 'Max Days', font: { size: 10 } }, grid: { color: 'rgba(255,255,255,0.04)' } },
      },
    },
  });
}

// ══════════════════════════════════════════════════════════════
// MODULE 2: RECOMMENDATION ENGINE
// ══════════════════════════════════════════════════════════════
document.getElementById('rec-budget').addEventListener('input', function () {
  document.getElementById('rec-budget-val').textContent = '₹' + parseInt(this.value).toLocaleString();
});
document.getElementById('rec-days').addEventListener('input', function () {
  document.getElementById('rec-days-val').textContent = this.value;
});

function computeSimilarity(tourist, dest, month) {
  let sim = 0;
  if (tourist.climate === dest.climate) sim += 0.40;
  if (tourist.style === dest.style) sim += 0.40;
  const totalCost = dest.cost * tourist.days;
  if (totalCost <= tourist.budget) {
    const slack = (tourist.budget - totalCost) / tourist.budget;
    sim += 0.20 * Math.min(slack + 0.5, 1.0);
  }
  return Math.min(sim, 1.0);
}

function runRecommendation() {
  const tourist = {
    name: document.getElementById('rec-name').value,
    climate: document.getElementById('rec-climate').value,
    style: document.getElementById('rec-style').value,
    budget: parseInt(document.getElementById('rec-budget').value),
    days: parseInt(document.getElementById('rec-days').value),
  };
  const month = parseInt(document.getElementById('rec-month').value);

  const scored = DESTINATIONS.map(d => {
    const sim = computeSimilarity(tourist, d, month);
    const ratingN = d.rating / 5.0;
    const seasonB = inSeason(d, month) ? 1.0 : 0.3;
    const finalScore = (0.45 * sim + 0.30 * ratingN + 0.15 * seasonB + 0.10 * 0.5) * 100;

    let reasons = [];
    if (tourist.climate === d.climate) reasons.push(`${getClimateEmoji(d.climate)} climate match`);
    if (tourist.style === d.style) reasons.push(`${d.style} style match`);
    if (d.cost * tourist.days <= tourist.budget) reasons.push('within budget');
    if (inSeason(d, month)) reasons.push('best season');

    return { dest: d, score: parseFloat(finalScore.toFixed(1)), reason: reasons.join(' · '), bestSeason: inSeason(d, month) };
  }).sort((a, b) => b.score - a.score).slice(0, 5);

  // Render results
  const container = document.getElementById('recResults');
  container.innerHTML = '';
  scored.forEach((item, i) => {
    const el = document.createElement('div');
    el.className = 'rec-item';
    el.innerHTML = `
      <div class="rec-rank">${i + 1}</div>
      <div class="rec-details">
        <div class="rec-name">${item.dest.emoji} ${item.dest.name} <span style="font-size:0.7rem;color:#5a5b78">(${item.dest.country})</span></div>
        <div class="rec-reason">${item.reason}</div>
      </div>
      <div class="rec-right">
        <div class="rec-score">${item.score}</div>
        <div class="rec-season">${item.bestSeason ? '✅ Peak season' : '⚠️ Off-peak'}</div>
      </div>
    `;
    container.appendChild(el);
  });

  // Score comparison chart
  destroyChart('recChart');
  makeChart('recChart', {
    type: 'bar',
    data: {
      labels: scored.map(r => r.dest.name),
      datasets: [{
        label: 'Match Score',
        data: scored.map(r => r.score),
        backgroundColor: [C.purple, C.pink, C.teal, C.amber, C.blue].map(c => alpha(c, 0.75)),
        borderColor: [C.purple, C.pink, C.teal, C.amber, C.blue],
        borderWidth: 2, borderRadius: 6,
      }],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { color: 'rgba(255,255,255,0.04)' } },
        y: { grid: { color: 'rgba(255,255,255,0.04)' }, min: 0, max: 100 },
      },
    },
  });
}

// ══════════════════════════════════════════════════════════════
// MODULE 3: FORECAST ENGINE
// ══════════════════════════════════════════════════════════════
let forecastChartInst = null;

function initForecastCharts() {
  updateForecast();
}

function updateForecast() {
  const key = document.getElementById('forecastDest').value;
  const sd = SEASONAL[key];
  if (!sd) return;

  document.getElementById('forecastTitle').textContent = `${sd.name} — Seasonal Visitor Trend`;
  document.getElementById('forecastBadge').textContent = sd.trend;

  const peak = Math.max(...sd.forecast);
  const peakMonth = MONTHS[sd.forecast.indexOf(peak)];
  const overallAvg = sd.historical.reduce((a, b) => a + b, 0) / 12;
  const forecastAvg = sd.forecast.reduce((a, b) => a + b, 0) / 12;
  const growth = (((forecastAvg - overallAvg) / overallAvg) * 100).toFixed(1);

  // Stats
  const statsEl = document.getElementById('forecastStats');
  statsEl.innerHTML = `
    <div class="stat-card" style="--accent:#7c6ef7">
      <div class="stat-icon">📅</div>
      <div class="stat-body"><div class="stat-value">${peakMonth}</div><div class="stat-label">Peak Month</div></div>
      <div class="stat-trend up">${sd.trend}</div>
    </div>
    <div class="stat-card" style="--accent:#00d2b4">
      <div class="stat-icon">👥</div>
      <div class="stat-body"><div class="stat-value">${(peak / 1000).toFixed(1)}K</div><div class="stat-label">Peak Forecast 2025</div></div>
      <div class="stat-trend up">Peak visitors</div>
    </div>
    <div class="stat-card" style="--accent:#f06a9b">
      <div class="stat-icon">📊</div>
      <div class="stat-body"><div class="stat-value">${growth}%</div><div class="stat-label">YoY Growth</div></div>
      <div class="stat-trend up">Year-over-year</div>
    </div>
    <div class="stat-card" style="--accent:#f9ae3b">
      <div class="stat-icon">🔻</div>
      <div class="stat-body"><div class="stat-value">${MONTHS[sd.forecast.indexOf(Math.min(...sd.forecast))]}</div><div class="stat-label">Lowest Month</div></div>
      <div class="stat-trend up">Off-season</div>
    </div>
  `;

  // Main trend line chart
  makeChart('forecastChart', {
    type: 'line',
    data: {
      labels: MONTHS,
      datasets: [
        {
          label: 'Historical Avg (2022-24)',
          data: sd.historical,
          borderColor: C.teal,
          backgroundColor: alpha(C.teal, 0.08),
          borderWidth: 2.5,
          tension: 0.4,
          fill: true,
          pointRadius: 4,
          pointBackgroundColor: C.teal,
        },
        {
          label: '2025 Forecast',
          data: sd.forecast,
          borderColor: C.pink,
          backgroundColor: alpha(C.pink, 0.06),
          borderWidth: 2.5,
          borderDash: [8, 4],
          tension: 0.4,
          fill: true,
          pointRadius: 4,
          pointBackgroundColor: C.pink,
        },
      ],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { labels: { font: { size: 11 } } } },
      scales: {
        x: { grid: { color: 'rgba(255,255,255,0.04)' } },
        y: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { callback: v => `${(v / 1000).toFixed(0)}K` } },
      },
      interaction: { intersect: false, mode: 'index' },
    },
  });

  // Monthly avg bar
  makeChart('monthlyAvgChart', {
    type: 'bar',
    data: {
      labels: MONTHS,
      datasets: [{
        label: 'Avg Visitors',
        data: sd.historical,
        backgroundColor: sd.historical.map(v => {
          const ratio = v / Math.max(...sd.historical);
          const r = Math.round(124 + (240 - 124) * ratio);
          const g = Math.round(110 + (106 - 110) * ratio);
          const b = Math.round(247 + (155 - 247) * ratio);
          return `rgba(${r},${g},${b},0.75)`;
        }),
        borderRadius: 5, borderWidth: 0,
      }],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { color: 'rgba(255,255,255,0.03)' } },
        y: { grid: { color: 'rgba(255,255,255,0.03)' }, ticks: { callback: v => `${(v / 1000).toFixed(0)}K` } },
      },
    },
  });

  // Seasonal index
  const overallMean = sd.historical.reduce((a, b) => a + b) / 12;
  const seaIdx = sd.historical.map(v => parseFloat((v / overallMean).toFixed(2)));
  makeChart('seasonalIndexChart', {
    type: 'line',
    data: {
      labels: MONTHS,
      datasets: [
        { label: 'Seasonal Index', data: seaIdx, borderColor: C.amber, backgroundColor: alpha(C.amber, 0.1), borderWidth: 2, tension: 0.4, fill: true, pointRadius: 4 },
        { label: 'Baseline (1.0)', data: MONTHS.map(() => 1), borderColor: 'rgba(255,255,255,0.15)', borderWidth: 1, borderDash: [4, 4], pointRadius: 0 },
      ],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { labels: { font: { size: 10 } } } },
      scales: {
        x: { grid: { color: 'rgba(255,255,255,0.03)' } },
        y: { grid: { color: 'rgba(255,255,255,0.03)' }, min: 0 },
      },
    },
  });
}

// ══════════════════════════════════════════════════════════════
// MODULE 4: OPTIMIZATION
// ══════════════════════════════════════════════════════════════
function showOptError(msg) {
  const c = document.getElementById('optResults');
  c.innerHTML = `<div class="placeholder-msg" style="color:#ff6b6b;border-color:rgba(255,107,107,0.3)">⚠️ ${msg}</div>`;
}

function updateOptTourist() {
  const tourist = TOURISTS[parseInt(document.getElementById('opt-tourist').value)];
  // Smart pre-selection: pick up to 5 destinations matching climate/style that fit budget
  let count = 0;
  document.querySelectorAll('#destCheckboxes input').forEach(cb => {
    const d = DESTINATIONS.find(x => x.id === parseInt(cb.value));
    if (!d) return;
    const affordable = d.cost * 3 <= tourist.budget * 0.5; // at least 3 days within half the budget
    const matches = d.climate === tourist.climate || d.style === tourist.style;
    cb.checked = matches && affordable && count < 8;
    if (cb.checked) count++;
  });
  // Ensure at least 3 checked
  if (count < 3) {
    document.querySelectorAll('#destCheckboxes input').forEach(cb => {
      if (!cb.checked && count < 3) { cb.checked = true; count++; }
    });
  }
}

function buildDestCheckboxes() {
  const grid = document.getElementById('destCheckboxes');
  grid.innerHTML = '';
  DESTINATIONS.forEach(d => {
    const item = document.createElement('div');
    item.className = 'checkbox-item';
    item.innerHTML = `
      <input type="checkbox" id="cb-${d.id}" value="${d.id}" />
      <label for="cb-${d.id}">${d.emoji} ${d.name} (${d.country}) — ₹${d.cost}/day</label>
    `;
    grid.appendChild(item);
  });
  // Pre-select affordable, varied picks
  [1, 6, 9, 11, 12].forEach(id => {
    const cb = document.getElementById('cb-' + id);
    if (cb) cb.checked = true;
  });
}

function nearestNeighborTSP(dests) {
  if (dests.length <= 1) return dests;
  let unvisited = [...dests];
  const route = [];
  // Start from westernmost destination
  let current = unvisited.reduce((a, b) => a.lon < b.lon ? a : b);
  unvisited = unvisited.filter(d => d.id !== current.id);
  route.push(current);
  while (unvisited.length) {
    const cur = current;
    const nearest = unvisited.reduce((best, d) => {
      const dist = haversine(cur.lat, cur.lon, d.lat, d.lon);
      return dist < haversine(cur.lat, cur.lon, best.lat, best.lon) ? d : best;
    });
    unvisited = unvisited.filter(d => d.id !== nearest.id);
    route.push(nearest);
    current = nearest;
  }
  return route;
}

function runOptimization() {
  const tourist = TOURISTS[parseInt(document.getElementById('opt-tourist').value)];
  const selected = DESTINATIONS.filter(d => {
    const cb = document.getElementById('cb-' + d.id);
    return cb && cb.checked;
  });

  if (selected.length < 2) {
    showOptError('Please select at least 2 destinations to optimize a route.');
    return;
  }

  // ── GREEDY budget-aware selection ──────────────────────────
  // Sort by value = rating / cost  (best bang for buck first)
  const byValue = [...selected].sort((a, b) =>
    (b.rating / Math.max(b.cost, 1)) - (a.rating / Math.max(a.cost, 1))
  );

  const chosen = [];
  let budgetLeft = tourist.budget;
  let daysLeft = tourist.days;
  const MIN_DAYS = 2; // minimum nights per stop

  for (const d of byValue) {
    if (chosen.length >= 8) break;
    const minCost = d.cost * MIN_DAYS;
    if (daysLeft >= MIN_DAYS && minCost <= budgetLeft) {
      chosen.push(d);
      budgetLeft -= minCost;
      daysLeft -= MIN_DAYS;
    }
  }

  // Fallback: if greedy filtered everything out, just take first 3 selected sorted by cost
  if (chosen.length === 0) {
    const fallback = [...selected].sort((a, b) => a.cost - b.cost).slice(0, 3);
    fallback.forEach(d => chosen.push(d));
  }

  // ── TSP route ordering ─────────────────────────────────────
  const route = nearestNeighborTSP(chosen);

  // ── Allocate days proportional to rating ──────────────────
  const totalRating = route.reduce((s, d) => s + d.rating, 0);
  const baseDays = MIN_DAYS;
  const remainingDays = Math.max(0, tourist.days - baseDays * route.length);

  const stops = route.map(d => {
    const extra = Math.floor(remainingDays * (d.rating / totalRating));
    const days = baseDays + extra;
    return { dest: d, days, cost: d.cost * days };
  });

  const totalCost = stops.reduce((s, st) => s + st.cost, 0);
  const totalDays = stops.reduce((s, st) => s + st.days, 0);

  // Total distance
  let totalDist = 0;
  for (let i = 1; i < stops.length; i++) {
    totalDist += haversine(
      stops[i - 1].dest.lat, stops[i - 1].dest.lon,
      stops[i].dest.lat, stops[i].dest.lon
    );
  }

  // ── Optimization score ─────────────────────────────────────
  const budgetUsed = totalCost / tourist.budget;
  const budgetScore = budgetUsed >= 0.60 && budgetUsed <= 0.98 ? 40
    : budgetUsed > 0.40 ? 25 : 15;
  const avgDist = stops.length > 1 ? totalDist / (stops.length - 1) : 0;
  const routeScore = avgDist < 1500 ? 30 : avgDist < 4000 ? 20 : 10;
  const daysScore = (totalDays / tourist.days) >= 0.75 ? 30 : 20;
  const score = Math.min(budgetScore + routeScore + daysScore, 100);

  // ── Render route ───────────────────────────────────────────
  const container = document.getElementById('optResults');
  container.innerHTML = '';
  const routeDiv = document.createElement('div');
  routeDiv.className = 'opt-route';

  stops.forEach((st, i) => {
    const stopDiv = document.createElement('div');
    stopDiv.className = 'opt-stop';
    stopDiv.style.animationDelay = (i * 0.08) + 's';
    stopDiv.innerHTML = `
      <div class="opt-stop-left">
        <div class="opt-dot"></div>
        ${i < stops.length - 1 ? '<div class="opt-line"></div>' : ''}
      </div>
      <div class="opt-stop-body">
        <div class="opt-stop-name">${st.dest.emoji} Stop ${i + 1}: ${st.dest.name}</div>
        <div class="opt-stop-meta">
          <span>${st.dest.country}</span>
          <span>&#8226; <strong>${st.days} days</strong></span>
          <span>&#8226; ₹${st.dest.cost.toLocaleString()} / day</span>
          <span class="opt-stop-cost">Stop Total: <strong>₹${st.cost.toLocaleString()}</strong></span>
        </div>
      </div>
    `;
    routeDiv.appendChild(stopDiv);
  });

  const sumDiv = document.createElement('div');
  sumDiv.className = 'opt-summary';
  sumDiv.innerHTML = `
    <div class="opt-sum-item"><div class="opt-sum-val">₹${totalCost.toLocaleString()}</div><div class="opt-sum-label">Total Cost</div></div>
    <div class="opt-sum-item"><div class="opt-sum-val">${totalDays} days</div><div class="opt-sum-label">Trip Duration</div></div>
    <div class="opt-sum-item"><div class="opt-sum-val">${Math.round(totalDist).toLocaleString()} km</div><div class="opt-sum-label">Route Distance</div></div>
    <div class="opt-sum-item"><div class="opt-sum-val" style="color:var(--teal)">${score}/100</div><div class="opt-sum-label">Optimization Score</div></div>
  `;
  // Make summary 4 columns
  sumDiv.style.gridTemplateColumns = 'repeat(4,1fr)';

  container.appendChild(routeDiv);
  container.appendChild(sumDiv);

  // ── Explicit Stop & Cost Breakdown Table ──────────────────
  const tableDiv = document.createElement('div');
  tableDiv.className = 'table-wrap';
  tableDiv.style.marginTop = '20px';
  tableDiv.innerHTML = `
    <h4 style="margin-bottom:10px;font-size:0.85rem;color:var(--text-primary)">Detailed Cost & Stop Breakdown</h4>
    <table class="data-table">
      <thead>
        <tr>
          <th style="text-align:left">Stop #</th>
          <th style="text-align:left">Destination</th>
          <th style="text-align:right">Days Spent</th>
          <th style="text-align:right">Cost per Day</th>
          <th style="text-align:right">Stop Total</th>
        </tr>
      </thead>
      <tbody>
        ${stops.map((st, i) => `
          <tr>
            <td>${i + 1}</td>
            <td><strong>${st.dest.emoji} ${st.dest.name}</strong></td>
            <td style="text-align:right">${st.days} days</td>
            <td style="text-align:right;color:var(--text-secondary)">₹${st.dest.cost.toLocaleString()}</td>
            <td style="text-align:right;color:var(--teal)"><strong>₹${st.cost.toLocaleString()}</strong></td>
          </tr>
        `).join('')}
      </tbody>
    </table>
  `;
  container.appendChild(tableDiv);

  // ── Charts ─────────────────────────────────────────────────
  document.getElementById('optChartCard').style.display = 'block';

  makeChart('optCostChart', {
    type: 'bar',
    data: {
      labels: stops.map(s => s.dest.name),
      datasets: [{
        label: 'Cost (INR)', data: stops.map(s => s.cost),
        backgroundColor: stops.map((_, i) => alpha([C.purple, C.pink, C.teal, C.amber, C.blue, C.red][i % 6], 0.75)),
        borderRadius: 6, borderWidth: 0
      }],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { maxRotation: 30, font: { size: 10 } } },
        y: { grid: { color: 'rgba(255,255,255,0.04)' } }
      }
    },
  });

  makeChart('optDaysChart', {
    type: 'doughnut',
    data: {
      labels: stops.map(s => s.dest.name),
      datasets: [{
        data: stops.map(s => s.days),
        backgroundColor: [C.purple, C.pink, C.teal, C.amber, C.blue, C.red].map(c => alpha(c, 0.8)),
        borderWidth: 0, hoverOffset: 8
      }],
    },
    options: {
      responsive: true, maintainAspectRatio: false, cutout: '60%',
      plugins: { legend: { position: 'right', labels: { font: { size: 10 } } } }
    },
  });
}

// ══════════════════════════════════════════════════════════════
// MODULE 5: REPORTS
// ══════════════════════════════════════════════════════════════
function initReportsCharts() {
  // Scatter: cost vs rating
  makeChart('scatterChart', {
    type: 'scatter',
    data: {
      datasets: [{
        label: 'Destinations',
        data: DESTINATIONS.map(d => ({ x: d.cost, y: d.rating, label: d.name })),
        backgroundColor: DESTINATIONS.map(d => alpha(getCardColor(d.style), 0.7)),
        pointRadius: 9, pointHoverRadius: 12,
        borderColor: DESTINATIONS.map(d => getCardColor(d.style)),
        borderWidth: 2,
      }],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: { callbacks: { label: ctx => `${ctx.raw.label}: ⭐${ctx.raw.y} / ₹${ctx.raw.x}/day` } },
      },
      scales: {
        x: { title: { display: true, text: 'Cost/Day (INR)', font: { size: 10 } }, grid: { color: 'rgba(255,255,255,0.04)' } },
        y: { title: { display: true, text: 'Rating', font: { size: 10 } }, grid: { color: 'rgba(255,255,255,0.04)' }, min: 4, max: 5.2 },
      },
    },
  });

  // Climate pie
  const climates = {};
  DESTINATIONS.forEach(d => climates[d.climate] = (climates[d.climate] || 0) + 1);
  makeChart('climateChart', {
    type: 'pie',
    data: {
      labels: Object.keys(climates).map(c => c.charAt(0).toUpperCase() + c.slice(1)),
      datasets: [{ data: Object.values(climates), backgroundColor: [C.pink, C.teal, C.blue, C.amber], borderWidth: 0, hoverOffset: 8 }],
    },
    options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom', labels: { padding: 12 } } } },
  });

  // Table
  buildTable();
  buildReportFiles();
}

function buildTable() {
  const table = document.getElementById('destTable');
  const sorted = [...DESTINATIONS].sort((a, b) => b.rating - a.rating);
  table.innerHTML = `
    <thead>
      <tr>
        <th>#</th><th>Destination</th><th>Country</th><th>Climate</th>
        <th>Style</th><th>Rating</th><th>Cost/Day</th><th>Best Season</th>
      </tr>
    </thead>
    <tbody>
      ${sorted.map((d, i) => `
        <tr>
          <td><span style="color:#5a5b78">${i + 1}</span></td>
          <td>${d.emoji} ${d.name}</td>
          <td>${d.country}</td>
          <td><span style="color:${getCardColor(d.style)}">${getClimateEmoji(d.climate)} ${d.climate}</span></td>
          <td><span style="color:${getCardColor(d.style)}">${d.style}</span></td>
          <td><span style="color:#f9ae3b">★ ${d.rating}</span></td>
          <td><span style="color:#00d2b4">$${d.cost}</span></td>
          <td>${MONTHS[d.seasonStart - 1]}–${MONTHS[d.seasonEnd - 1]}</td>
        </tr>
      `).join('')}
    </tbody>
  `;
}

function buildReportFiles() {
  const files = [
    { icon: '📊', name: 'itinerary_cost_1.png', desc: 'Cost Breakdown Chart' },
    { icon: '🥧', name: 'itinerary_days_1.png', desc: 'Days Allocation Pie' },
    { icon: '📈', name: 'seasonal_trend_1.png', desc: 'Bali Seasonal Trend' },
    { icon: '📈', name: 'seasonal_trend_2.png', desc: 'Paris Seasonal Trend' },
    { icon: '📈', name: 'seasonal_trend_5.png', desc: 'Reykjavik Seasonal Trend' },
    { icon: '⭐', name: 'destination_ratings.png', desc: 'Destination Ratings' },
    { icon: '🎯', name: 'recommendations_Alice_Johnson.png', desc: 'Recommendation Scores' },
  ];
  const container = document.getElementById('reportFiles');
  container.innerHTML = files.map(f => `
    <div class="report-file">
      <span class="report-file-icon">${f.icon}</span>
      <div>
        <div style="font-weight:600;color:#f0f0f8">${f.name}</div>
        <div style="font-size:0.68rem;margin-top:1px">${f.desc}</div>
      </div>
    </div>
  `).join('');
}

// ══════════════════════════════════════════════════════════════
// INIT DASHBOARD
// ══════════════════════════════════════════════════════════════
function initDashboardInteractions() {
  const cards = document.querySelectorAll('#section-dashboard .dash-grid-4 .card');

  cards.forEach(card => {
    // Maximize on double click
    card.addEventListener('dblclick', function () {
      if (this.classList.contains('fullscreen')) {
        this.classList.remove('fullscreen');
      } else {
        // remove fullscreen from all others
        cards.forEach(c => c.classList.remove('fullscreen'));
        this.classList.add('fullscreen');
      }
      // Trigger chart resize
      setTimeout(() => window.dispatchEvent(new Event('resize')), 50);
    });
  });
}

(function init() {
  // Set default date for optimization
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 30);
  document.getElementById('opt-date').value = tomorrow.toISOString().split('T')[0];

  renderDestinations(DESTINATIONS);
  buildDestCheckboxes();
  initDashboardCharts();
  initDashboardInteractions();
})();
