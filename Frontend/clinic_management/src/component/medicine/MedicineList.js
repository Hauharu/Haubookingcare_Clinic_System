import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Container, Table, Spinner } from "react-bootstrap";
import Apis, { endpoint } from "../../configs/Apis";

const MedicineList = () => {
  const { t } = useTranslation();
  const [medicines, setMedicines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState({ name: '', unit: '', description: '' });
  const [page, setPage] = useState(1);
  const PAGE_SIZE = 8; // 2 hàng x 4 cột

  useEffect(() => {
    const fetchMedicines = async () => {
      setLoading(true);
      try {
        const res = await Apis.get(endpoint["medicines"]);
        if (res.data && res.data.result && Array.isArray(res.data.result.content)) {
          setMedicines(res.data.result.content);
        } else if (res.data && Array.isArray(res.data.result)) {
          setMedicines(res.data.result);
        } else if (Array.isArray(res.data)) {
          setMedicines(res.data);
        } else {
          setMedicines([]);
        }
      } catch (error) {
        setMedicines([]);
      } finally {
        setLoading(false);
      }
    };
    fetchMedicines();
  }, []);

  // Lọc theo từng trường
  const filtered = medicines.filter(m =>
    m.name.toLowerCase().includes(search.name.toLowerCase()) &&
    m.unit.toLowerCase().includes(search.unit.toLowerCase()) &&
    (m.description ? m.description.toLowerCase().includes(search.description.toLowerCase()) : true)
  );
  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paginated = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);


  return (
    <Container className="mt-4">
      <h4 className="mb-3">{t("medicine.listTitle")}</h4>
      <div className="mb-3 d-flex gap-2">
        <input
          type="text"
          placeholder={t("medicine.searchByName")}
          value={search.name}
          onChange={e => setSearch({ ...search, name: e.target.value })}
          className="form-control"
          style={{ maxWidth: 200 }}
        />
        <input
          type="text"
          placeholder={t("medicine.searchByUnit")}
          value={search.unit}
          onChange={e => setSearch({ ...search, unit: e.target.value })}
          className="form-control"
          style={{ maxWidth: 150 }}
        />
        <input
          type="text"
          placeholder={t("medicine.searchByDescription")}
          value={search.description}
          onChange={e => setSearch({ ...search, description: e.target.value })}
          className="form-control"
          style={{ maxWidth: 250 }}
        />
      </div>
      {loading ? (
        <Spinner animation="border" />
      ) : (
        <>
          {paginated.length === 0 ? (
            <div className="text-muted">{t("medicine.noMatch")}</div>
          ) : (
            <div className="row">
              {paginated.map((m, idx) => (
                <div key={m.id} className="col-md-3 col-sm-6 mb-4">
                  <div className="card shadow-sm h-100">
                    <div className="card-body">
                      <h5 className="card-title text-primary">{m.name}</h5>
                      <p className="mb-1"><strong>{t("medicine.unit")}:</strong> {m.unit}</p>
                      <p className="mb-1"><strong>{t("medicine.price")}:</strong> {m.pricePerUnit}</p>
                      <p className="mb-1"><strong>{t("medicine.description")}:</strong> {m.description}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
          {/* Phân trang */}
          {totalPages > 1 && (
            <div className="d-flex justify-content-center align-items-center gap-2 mt-2">
              <button
                className="btn btn-outline-primary btn-sm"
                disabled={page === 1}
                onClick={() => setPage(page - 1)}
              >
                &lt; {t("medicine.prev")}
              </button>
              <span>{t("medicine.page")}: {page} / {totalPages}</span>
              <button
                className="btn btn-outline-primary btn-sm"
                disabled={page === totalPages}
                onClick={() => setPage(page + 1)}
              >
                {t("medicine.next")} &gt;
              </button>
            </div>
          )}
        </>
      )}
    </Container>
  );
}

export default MedicineList;
