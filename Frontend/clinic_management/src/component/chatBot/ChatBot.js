import React, { useState, useRef } from "react";
import { Card, Button, Form, Spinner } from "react-bootstrap";
import axios from "axios";
import { AI_URL, OPENROUTER_API_KEY } from "../../configs/Apis";


const ChatBot = () => {
    const [messages, setMessages] = useState([
        { role: "assistant", content: "Xin chào! Tôi là trợ lý AI tư vấn sức khỏe. Bạn cần hỗ trợ gì?" }
    ]);

    const MODEL = "deepseek/deepseek-chat-v3-0324:free";
    const [input, setInput] = useState("");
    const [loading, setLoading] = useState(false);
    const [isOpen, setIsOpen] = useState(false); // State to control chatbot visibility
    const messagesEndRef = useRef(null);


    const sendMessage = async (e) => {
        e.preventDefault();
        if (!input.trim()) return;

        const newMessages = [...messages, { role: "user", content: input }];
        setMessages(newMessages);
        setInput("");
        setLoading(true);

        try {
            const res = await axios.post(AI_URL, {
                model: MODEL,
                messages: newMessages.map(m => ({ role: m.role, content: m.content }))
            }, {
                headers: {
                    "Authorization": `Bearer ${OPENROUTER_API_KEY}`,
                    "Content-Type": "application/json"
                }
            });

            const aiReply = res.data.choices?.[0]?.message?.content || "Xin lỗi, tôi chưa hiểu ý bạn.";
            setMessages([...newMessages, { role: "assistant", content: aiReply }]);
        } catch (err) {
            const errorMessage = err.response?.data?.message || "Có lỗi xảy ra, vui lòng thử lại sau.";
            setMessages([...newMessages, { role: "assistant", content: errorMessage }]);
        } finally {
            setLoading(false);
            setTimeout(() => {
                messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
            }, 100);
        }
    };

    const handleOutsideClick = (e) => {
        if (!e.target.closest(".chatbot-card") && !e.target.closest(".chatbot-button")) {
            setIsOpen(false);
        }
    };

    return (
        <div onClick={handleOutsideClick} style={{ position: "relative" }}>
            {!isOpen && (
                <Button
                    className="chatbot-button"
                    onClick={(e) => {
                        e.stopPropagation();
                        setIsOpen(true);
                    }}
                    style={{
                        position: "fixed",
                        bottom: 24,
                        right: 24,
                        zIndex: 10000,
                        backgroundColor: "transparent",
                        border: "none",
                        width: 300,
                        height: 250,
                        padding: 0,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        boxShadow: "none" 
                    }}
                >
                    <img
                        src="/assets/images/preny_2.gif"
                        alt="Chatbot Animation"
                        style={{
                            width: "100%",
                            height: "100%",
                            cursor: "pointer" 
                        }}
                    />
                </Button>
            )}

            {isOpen && (
                <Card
                    className="chatbot-card"
                    style={{
                        position: "fixed",
                        bottom: 80,
                        right: 24,
                        width: 340,
                        zIndex: 9999,
                        boxShadow: "0 4px 24px rgba(0,0,0,0.15)",
                        backgroundColor: "white" // Ensure text visibility
                    }}
                >
                    <Card.Header className="bg-primary text-white py-2">
                        <b><i className="bi bi-robot me-2"></i>AI Tư vấn sức khỏe</b>
                        <Button
                            onClick={(e) => {
                                e.stopPropagation();
                                setIsOpen(false);
                            }}
                            variant="light"
                            style={{ float: "right", padding: "0 8px" }}
                        >
                            X
                        </Button>
                    </Card.Header>
                    <Card.Body style={{ maxHeight: 320, overflowY: "auto", background: "#f8f9fa", padding: 12 }}>
                        {messages.map((msg, idx) => (
                            <div key={idx} style={{ textAlign: msg.role === "user" ? "right" : "left", marginBottom: 8 }}>
                                <span
                                    style={{
                                        display: "inline-block",
                                        background: msg.role === "user" ? "#0d6efd" : "#e9ecef",
                                        color: msg.role === "user" ? "#fff" : "#333",
                                        borderRadius: 12,
                                        padding: "6px 12px",
                                        maxWidth: "80%",
                                        wordBreak: "break-word"
                                    }}
                                >
                                    {msg.content}
                                </span>
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </Card.Body>
                    <Card.Footer className="p-2">
                        <Form onSubmit={sendMessage} className="d-flex gap-2">
                            <Form.Control
                                type="text"
                                placeholder="Nhập câu hỏi..."
                                value={input}
                                onChange={e => setInput(e.target.value)}
                                disabled={loading}
                                autoFocus
                            />
                            <Button type="submit" variant="primary" disabled={loading || !input.trim()}>
                                {loading ? <Spinner size="sm" animation="border" /> : <i className="bi bi-send"></i>}
                            </Button>
                        </Form>
                    </Card.Footer>
                </Card>
            )}
        </div>
    );
};

export default ChatBot;

