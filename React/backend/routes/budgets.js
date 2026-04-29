const express = require('express');
const auth = require('../middleware/auth');
const budgetController = require('../controllers/budgetController');
const router = express.Router();

router.use(auth);
router.post('/', budgetController.setBudget);
//router.get('/list', budgetController.getAllBudgets);
router.get('/current-spending', budgetController.getCurrentMonthSpending);
router.get('/:month_year', budgetController.getBudget);
router.put('/:month_year', budgetController.updateBudget);
router.delete('/:month_year', budgetController.deleteBudget);
router.get('/list', budgetController.getAllBudgets);
router.get('/list', budgetController.getAllBudgets);
module.exports = router;