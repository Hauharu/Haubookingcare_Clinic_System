import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { Container, Card, Spinner } from "react-bootstrap";
import Apis, { endpoint } from "../../configs/Apis";

const MedicineDetail = () => {
  const { id } = useParams();
  const [medicine, setMedicine] = useState(null);
  const [loading, setLoading] = useState(true);
  const { t } = useTranslation();

  useEffect(() => {
    const fetchMedicine = async () => {
      setLoading(true);
      try {
        const res = await Apis.get(`${endpoint["medicines"]}/${id}`);
        setMedicine(res.data && res.data.result ? res.data.result : null);
      } catch (error) {
        // Có thể log lỗi ra toast hoặc gửi về server nếu cần
      } finally {
        setLoading(false);
      }
    };
    fetchMedicine();
  }, [id]);

  if (loading) return <Spinner animation="border" />;
  if (!medicine) return <div>{t("medicine.notFound")}</div>;

  return (
    <Container className="mt-4">
      <Card>
        <Card.Body>
          <Card.Title>{medicine.name}</Card.Title>
          <Card.Text><strong>{t("medicine.unit")}:</strong> {medicine.unit}</Card.Text>
          <Card.Text><strong>{t("medicine.price")}:</strong> {medicine.pricePerUnit}</Card.Text>
          <Card.Text><strong>{t("medicine.description")}:</strong> {medicine.description}</Card.Text>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default MedicineDetail;
