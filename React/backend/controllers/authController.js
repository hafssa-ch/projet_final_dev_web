const db = require('../database');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

exports.register = (req, res) => {
  const { email, password, name } = req.body;
  if (!email || !password || !name) return res.status(400).json({ message: 'Tous les champs sont requis' });
  const hashedPassword = bcrypt.hashSync(password, 10);
  db.run(`INSERT INTO users (email, password, name, role) VALUES (?, ?, ?, ?)`,
    [email, hashedPassword, name, 'user'],
    function(err) {
      if (err) return res.status(400).json({ message: 'Email déjà utilisé' });
      res.status(201).json({ message: 'Utilisateur créé', userId: this.lastID });
    });
};

exports.login = (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) return res.status(400).json({ message: 'Email et mot de passe requis' });
  db.get(`SELECT * FROM users WHERE email = ?`, [email], (err, user) => {
    if (err || !user) return res.status(401).json({ message: 'Identifiants invalides' });
    const valid = bcrypt.compareSync(password, user.password);
    if (!valid) return res.status(401).json({ message: 'Identifiants invalides' });
    const token = jwt.sign({ userId: user.id, role: user.role }, process.env.JWT_SECRET, { expiresIn: '24h' });
    res.json({ token, user: { id: user.id, name: user.name, email: user.email, role: user.role } });
  });
};