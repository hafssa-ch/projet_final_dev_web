require('dotenv').config();
const express = require('express');
const cors = require('cors');
const db = require('./database');

const app = express();

// Configuration CORS complète (définie avant utilisation)
const corsOptions = {
  origin: 'http://localhost:3000',            // autorise uniquement React
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization'],
  credentials: true,
  optionsSuccessStatus: 200
};

app.use(cors(corsOptions));  // une seule fois

// Optionnel : gérer les pré-requêtes OPTIONS manuellement (cors le fait déjà)
app.options('*', cors(corsOptions));

app.use(express.json());

// Routes API
app.use('/api/auth', require('./routes/auth'));
app.use('/api/expenses', require('./routes/expenses'));
app.use('/api/categories', require('./routes/categories'));
app.use('/api/budgets', require('./routes/budgets'));
app.use('/api/stats', require('./routes/stats'));

const PORT = process.env.PORT || 5000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Serveur démarré sur http://0.0.0.0:${PORT}`);
});