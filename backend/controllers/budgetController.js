const db = require('../database');

exports.setBudget = (req, res) => {
  const { month_year, amount } = req.body;
  if (amount < 0) return res.status(400).json({ message: 'Budget invalide' });
  db.run(`INSERT OR REPLACE INTO monthly_budgets (month_year, amount, user_id) VALUES (?,?,?)`,
    [month_year, amount, req.userId],
    function(err) {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ message: 'Budget enregistré' });
    });
};

exports.getBudget = (req, res) => {
  const { month_year } = req.params;
  db.get(`SELECT amount FROM monthly_budgets WHERE user_id=? AND month_year=?`, [req.userId, month_year], (err, row) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ amount: row ? row.amount : 0 });
  });
};

exports.getCurrentMonthSpending = (req, res) => {
  const now = new Date();
  const month_year = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}`;
  // Utilisation de substr pour extraire les 7 premiers caractères (YYYY-MM)
  db.get(
    `SELECT SUM(amount) as total FROM expenses WHERE user_id=? AND substr(date,1,7) = ?`,
    [req.userId, month_year],
    (err, row) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ spent: row.total || 0, month: month_year });
    }
  );
};

// Lister tous les budgets de l'utilisateur connecté
exports.getAllBudgets = (req, res) => {
  db.all(
    `SELECT month_year, amount FROM monthly_budgets 
     WHERE user_id = ? ORDER BY month_year DESC`,
    [req.userId],
    (err, rows) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json(rows);
    }
  );
};

// Créer ou remplacer un budget (ON CONFLICT REPLACE)
exports.setBudget = (req, res) => {
  const { month_year, amount } = req.body;
  if (amount < 0) return res.status(400).json({ message: 'Montant invalide' });
  db.run(
    `INSERT OR REPLACE INTO monthly_budgets (month_year, amount, user_id) VALUES (?, ?, ?)`,
    [month_year, amount, req.userId],
    function(err) {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ message: 'Budget enregistré', id: this.lastID });
    }
  );
};

// Mettre à jour un budget
exports.updateBudget = (req, res) => {
  const { month_year } = req.params;
  const { amount } = req.body;
  if (!amount || amount < 0) return res.status(400).json({ message: 'Montant invalide' });
  db.run(
    `UPDATE monthly_budgets SET amount = ? 
     WHERE user_id = ? AND month_year = ?`,
    [amount, req.userId, month_year],
    function(err) {
      if (err) return res.status(500).json({ error: err.message });
      if (this.changes === 0) return res.status(404).json({ message: 'Budget non trouvé' });
      res.json({ message: 'Budget mis à jour' });
    }
  );
};

// Supprimer un budget
exports.deleteBudget = (req, res) => {
  const { month_year } = req.params;
  db.run(
    `DELETE FROM monthly_budgets WHERE user_id = ? AND month_year = ?`,
    [req.userId, month_year],
    function(err) {
      if (err) return res.status(500).json({ error: err.message });
      if (this.changes === 0) return res.status(404).json({ message: 'Budget non trouvé' });
      res.json({ message: 'Budget supprimé' });
    }
  );
};