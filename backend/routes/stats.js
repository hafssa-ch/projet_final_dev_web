const express = require('express');
const auth = require('../middleware/auth');
const statsController = require('../controllers/statsController');
const router = express.Router();

router.use(auth);
router.get('/monthly', statsController.getMonthlyStats);
router.get('/category-breakdown', statsController.getCategoryBreakdown);
router.get('/budget-vs-actual', statsController.getBudgetVsActual);

module.exports = router;