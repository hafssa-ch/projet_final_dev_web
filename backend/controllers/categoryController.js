const db = require('../database');

exports.getCategories = (req, res) => {
  db.all(`SELECT * FROM expense_categories WHERE user_id = ? OR user_id IS NULL`, [req.userId], (err, rows) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(rows);
  });
};

exports.createCategory = (req, res) => {
  const { name, icon, color } = req.body;
  db.run(`INSERT INTO expense_categories (name, icon, color, user_id) VALUES (?,?,?,?)`,
    [name, icon || '', color || '#000000', req.userId],
    function(err) {
      if (err) return res.status(500).json({ error: err.message });
      res.status(201).json({ id: this.lastID });
    });
};

exports.updateCategory = (req, res) => {
  const { name, color } = req.body;
  const { id } = req.params;
  if (!name || name.trim() === '') {
    return res.status(400). json({ message: 'Le nom est requis' });
  }
  db.run(
    `UPDATE expense_categories SET name = ?, color = ? WHERE id = ? AND user_id = ?`,
    [name, color || '#000000', id, req.userId],
    function(err) {
      if (err) {
        return res.status(500).json({ error: err.message });
      }
      if (this.changes === 0) {
        return res.status(404).json({ message: 'Catégorie non trouvée ou non autorisée' });
      }
      res.json({ message: 'Catégorie modifiée' });
    }
  );
};


exports.deleteCategory = (req, res) => {
  db.get(`SELECT COUNT(*) as count FROM expenses WHERE category_id = ?`, [req.params.id], (err, row) => {
    if (err) return res.status(500).json({ error: err.message });
    if (row.count > 0) {
      return res.status(400).json({ message: 'Catégorie utilisée, supprimez d’abord les dépenses' });
    }
    db.run(`DELETE FROM expense_categories WHERE id=? AND user_id=?`, [req.params.id, req.userId], function(err) {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ message: 'Catégorie supprimée' });
    });
  });
};