const express = require("express");
const auth = require("../middleware/auth");
const validate = require("../middleware/validate");
const {
  createRequest,
  getRequests,
  getRequestById,
  updateRequestStatus,
} = require("../controllers/requestController");
const {
  createRequestValidation,
  updateRequestStatusValidation,
  requestIdValidation,
  requestListValidation,
} = require("../validators/requestValidator");

const router = express.Router();

router.post("/", auth, createRequestValidation, validate, createRequest);
router.get("/", auth, requestListValidation, validate, getRequests);
router.get("/:id", auth, requestIdValidation, validate, getRequestById);
router.put(
  "/:id/status",
  auth,
  updateRequestStatusValidation,
  validate,
  updateRequestStatus
);

module.exports = router;
