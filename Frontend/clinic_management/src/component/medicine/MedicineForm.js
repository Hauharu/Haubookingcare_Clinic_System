import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Form, Button, Container, Spinner } from "react-bootstrap";
import Apis, { endpoint } from "../../configs/Apis";

const MedicineForm = ({ initialData = {}, onSave, loading }) => {
  const [form, setForm] = useState({
    name: initialData.name || "",
    unit: initialData.unit || "",
    pricePerUnit: initialData.pricePerUnit || "",
    description: initialData.description || ""
  });
  const { t } = useTranslation();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await Apis.post(endpoint["medicines"], form);
      if (onSave) onSave(form);
    } catch (error) {
      // Có thể log lỗi ra toast hoặc gửi về server nếu cần
    }
  };

  return (
    <Container className="mt-4">
      <Form onSubmit={handleSubmit}>
        <Form.Group className="mb-3">
          <Form.Label>{t("medicine.name")}</Form.Label>
          <Form.Control name="name" value={form.name} onChange={handleChange} required />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>{t("medicine.unit")}</Form.Label>
          <Form.Control name="unit" value={form.unit} onChange={handleChange} required />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>{t("medicine.price")}</Form.Label>
          <Form.Control name="pricePerUnit" value={form.pricePerUnit} onChange={handleChange} required type="number" />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>{t("medicine.description")}</Form.Label>
          <Form.Control name="description" value={form.description} onChange={handleChange} as="textarea" />
        </Form.Group>
        <Button type="submit" variant="primary" disabled={loading}>
          {loading ? <Spinner animation="border" size="sm" /> : t("commonButton.save")}
        </Button>
      </Form>
    </Container>
  );
};

export default MedicineForm;
