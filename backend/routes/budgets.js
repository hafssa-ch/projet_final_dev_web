const express = require('express');
const auth = require('../middleware/auth');
const budgetController = require('../controllers/budgetController');
const router = express.Router();

router.use(auth);
router.post('/', budgetController.setBudget);           // Pour créer ou remplacer un budget
router.get('/list', budgetController.getAllBudgets);   // Pour obtenir la liste de tous les budgets
router.get('/:month_year', budgetController.getBudget); // Pour obtenir un budget spécifique
router.put('/:month_year', budgetController.updateBudget); // Pour modifier un budget existant
router.delete('/:month_year', budgetController.deleteBudget); // Pour supprimer un budget

module.exports = router;