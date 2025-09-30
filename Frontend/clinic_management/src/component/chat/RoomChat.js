import { useContext, useEffect, useRef, useState, useMemo, useCallback } from "react";
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { MyUserContext, MyDipatcherContext } from "../../configs/MyContexts";
import { authApis, endpoint, CLOUDINARY_PRESET, CLOUDINARY_URL } from "../../configs/Apis";
import { db } from "../../configs/FirebaseConfigs";
import { Button, Col, Container, Form, Row, Modal } from "react-bootstrap";
import { addDoc, collection, query, where, onSnapshot, orderBy, getDocs, limit, doc, updateDoc, deleteDoc } from "firebase/firestore";
import JitsiCall from "./JitsiCall";
import "./Styles/RoomChat.css";
const MemoizedAvatar = ({ src, alt, className, onLoad, onError }) => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [hasError, setHasError] = useState(false);
  
  const handleLoad = useCallback(() => {
    setIsLoaded(true);
    setHasError(false);
    onLoad?.();
  }, [onLoad]);
  
  const handleError = useCallback((e) => {
    setHasError(true);
    setIsLoaded(false);
    e.target.src = '/default-avatar.png';
    onError?.(e);
  }, [onError]);
  
  return (
    <div className="avatar-wrapper" style={{ position: 'relative' }}>
      {!isLoaded && !hasError && (
        <div 
          className={`${className} avatar-placeholder`}
          style={{
            backgroundColor: '#f0f0f0',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#999'
          }}
        >
          <i className="bi bi-person-fill"></i>
        </div>
      )}
      <img
        src={src}
        alt={alt}
        className={`${className} ${isLoaded ? 'avatar-loaded' : 'avatar-loading'}`}
        onLoad={handleLoad}
        onError={handleError}
        loading="lazy"
        style={{
          opacity: isLoaded ? 1 : 0,
          transition: 'opacity 0.2s ease',
          display: isLoaded ? 'block' : 'none'
        }}
      />
    </div>
  );
};

const RoomChat = () => {
  const { t } = useTranslation();
  const user = useContext(MyUserContext);
  const location = useLocation();
  const navigate = useNavigate();
  const params = useParams(); // Thêm để lấy URL params
  const [showCall, setShowCall] = useState(false);
  const [showCallModal, setShowCallModal] = useState(false);
  const [showImageConfirmModal, setShowImageConfirmModal] = useState(false);
  const [incomingCall, setIncomingCall] = useState(null); // Thông báo cuộc gọi đến
  const [currentCallId, setCurrentCallId] = useState(null); // ID cuộc gọi hiện tại
  const [currentRoomName, setCurrentRoomName] = useState(null); // Tên phòng call hiện tại
  const [isOnCall, setIsOnCall] = useState(false); // Đang trong cuộc gọi
  const [messages, setMessages] = useState([]);
  const [messageText, setMessageText] = useState("");
  const [imageFile, setImageFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [currentAvatar, setCurrentAvatar] = useState("");
  const [otherAvatar, setOtherAvatar] = useState("");
  const [otherUser, setOtherUser] = useState(null);
  const [userId, setUserId] = useState("");
  const [otherUserId, setOtherUserId] = useState("");
  const [showUserSelector, setShowUserSelector] = useState(false); // Thêm state cho UI chọn user
  const [tempOtherUserId, setTempOtherUserId] = useState(""); // Temporary ID để user nhập
  const messagesEndRef = useRef(null);

  // Removed PeerJS initialization - using Jitsi instead

  // Memoize avatars để tránh flickering - cải thiện logic
  const memoizedCurrentAvatar = useMemo(() => {
    const avatar = user?.result?.avatar || currentAvatar || '/default-avatar.png';
    // Đảm bảo avatar không thay đổi nếu chỉ là whitespace
    return avatar && avatar.trim() !== '' ? avatar.trim() : '/default-avatar.png';
  }, [user?.result?.avatar, currentAvatar]);

  const memoizedOtherAvatar = useMemo(() => {
    const avatar = otherUser?.avatar || otherAvatar || '/default-avatar.png';
    // Đảm bảo avatar không thay đổi nếu chỉ là whitespace  
    return avatar && avatar.trim() !== '' ? avatar.trim() : '/default-avatar.png';
  }, [otherUser?.avatar, otherAvatar]);

  // State để track avatar loading
  const [avatarLoaded, setAvatarLoaded] = useState({
    current: false,
    other: false
  });

  // Memoize rendered messages để tránh re-render không cần thiết
  const renderedMessages = useMemo(() => {
    return messages.map((msg) => {
      const isCurrentUser = msg.senderId === userId;
      
      // Cải thiện logic avatar để tránh flickering
      let senderAvatar;
      let senderName;

      if (isCurrentUser) {
        // Tin nhắn của user hiện tại - sử dụng avatar của chính mình
        senderAvatar = memoizedCurrentAvatar;
  senderName = `${user?.result?.firstName || ''} ${user?.result?.lastName || ''}`.trim() || t('roomChat.you');
      } else {
        // Tin nhắn của người khác - sử dụng avatar của người khác
        senderAvatar = memoizedOtherAvatar;
  senderName = `${otherUser?.firstName || ''} ${otherUser?.lastName || ''}`.trim() || t('roomChat.other');
      }
      
      // Đảm bảo avatar không bị empty
      const avatarSrc = senderAvatar && senderAvatar.trim() !== '' ? senderAvatar : '/default-avatar.png';
      
      // Format timestamp
      const formatTime = (timestamp) => {
        if (!timestamp) return '';
        const date = timestamp.toDate ? timestamp.toDate() : new Date(timestamp);
        const now = new Date();
        
        // Nếu trong ngày hôm nay, chỉ hiện giờ
        if (date.toDateString() === now.toDateString()) {
          return date.toLocaleTimeString('vi-VN', { 
            hour: '2-digit', 
            minute: '2-digit' 
          });
        } else {
          // Nếu khác ngày, hiện ngày tháng
          return date.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
          });
        }
      };
      
      const messageTime = formatTime(msg.createdAt);

      return (
        <div
          key={msg.messageId}
          className={`chat-message ${isCurrentUser ? "message-right" : "message-left"}`}
          style={{ alignItems: "flex-end" }}
        >
          {/* Avatar cho tin nhắn bên trái (người khác) */}
          {!isCurrentUser && (
            <MemoizedAvatar
              src={avatarSrc}
              alt={t('roomChat.avatar')}
              className="message-avatar"
            />
          )}
          
          {/* Nội dung tin nhắn */}
          <div style={{ maxWidth: '70%' }}>
            {!isCurrentUser && (
              <div className="message-sender">
                {senderName}
              </div>
            )}
            <div className={`bubble ${isCurrentUser ? "bubble-right" : "bubble-left"}`}>
              {msg.text && <p style={{ margin: 0 }}>{msg.text}</p>}
              {msg.imageUrl && (
                <img
                  src={msg.imageUrl}
                  alt="Hình ảnh"
                  className="message-image"
                  style={{ cursor: "pointer", maxWidth: "100%", maxHeight: 180, borderRadius: 10, objectFit: "cover", display: "block", marginTop: msg.text ? 8 : 0 }}
                  onClick={() => window.open(msg.imageUrl, "_blank")}
                  onError={e => { e.target.onerror = null; e.target.src = '/default-avatar.png'; }}
                />
              )}
            </div>
            {messageTime && (
              <div className={`message-time ${isCurrentUser ? 'text-end' : 'text-start'}`}>
                {messageTime}
              </div>
            )}
          </div>
          
          {/* Avatar cho tin nhắn bên phải (current user) */}
          {isCurrentUser && (
            <MemoizedAvatar
              src={avatarSrc}
              alt={t('roomChat.avatar')}
              className="message-avatar"
            />
          )}
        </div>
      );
    });
  }, [messages, userId, memoizedCurrentAvatar, memoizedOtherAvatar, user, otherUser]);

  useEffect(() => {
    if (!userId || !otherUserId) return;
    
    const q = query(
      collection(db, "messages"),
      orderBy("createdAt", "asc")
    );
    
    const unsubscribe = onSnapshot(q, (snapshot) => {
      const msgs = snapshot.docs.map(doc => ({ 
        ...doc.data(), 
        messageId: doc.id 
      }));
      
      const filtered = msgs.filter(m => {
        const isBetweenUsers = 
          (m.senderId === userId && m.receiverId === otherUserId) ||
          (m.senderId === otherUserId && m.receiverId === userId);
        return isBetweenUsers;
      });
      
      setMessages(filtered);
    }, (error) => {
      const simpleQuery = query(
        collection(db, "messages"),
        orderBy("createdAt", "asc")
      );
      
      const backupUnsubscribe = onSnapshot(simpleQuery, (snapshot) => {
        const msgs = snapshot.docs.map(doc => ({ ...doc.data(), messageId: doc.id }));
        const filtered = msgs.filter(m => {
          const isBetweenUsers = 
            (m.senderId === userId && m.receiverId === otherUserId) ||
            (m.senderId === otherUserId && m.receiverId === userId);
          return isBetweenUsers;
        });
        
        setMessages(filtered);
      });
      
      return () => backupUnsubscribe();
    });
    
    return () => unsubscribe();
  }, [userId, otherUserId]);

  useEffect(() => {
    if (!userId || !otherUserId) return;
    
    const incomingCallsQuery = query(
      collection(db, "videoCalls"),
      where("receiverId", "==", userId),
      where("status", "in", ["pending", "accepted", "rejected", "ended"])
    );
    
    const outgoingCallsQuery = query(
      collection(db, "videoCalls"),
      where("senderId", "==", userId),
      where("status", "in", ["pending", "accepted", "rejected", "ended"])
    );
    
    const unsubscribeIncoming = onSnapshot(incomingCallsQuery, (snapshot) => {
      snapshot.docChanges().forEach((change) => {
        const callData = { ...change.doc.data(), id: change.doc.id };
        
        if (callData.senderId !== otherUserId) return;
        
        if (change.type === "added" && callData.status === "pending") {
          setIncomingCall(callData);
          setShowCallModal(true);
          
          try {
            const audio = new Audio('/notification-sound.mp3');
            audio.play().catch(() => {});
          } catch (e) {}
        }
        
        if (change.type === "modified") {
          if (callData.status === "ended") {
            setShowCall(false);
            setIsOnCall(false);
            setCurrentCallId(null);
            setCurrentRoomName(null);
            setIncomingCall(null);
            setShowCallModal(false);
          }
        }
      });
    });
    
    const unsubscribeOutgoing = onSnapshot(outgoingCallsQuery, (snapshot) => {
      snapshot.docChanges().forEach((change) => {
        const callData = { ...change.doc.data(), id: change.doc.id };
        
        if (callData.receiverId !== otherUserId) return;
        
        if (change.type === "modified") {
          if (callData.status === "accepted") {
            setCurrentRoomName(callData.roomName);
            setShowCall(true);
            setIsOnCall(true);
            setShowCallModal(false);
            setCurrentCallId(callData.id);
          } else if (callData.status === "rejected") {
            alert("Cuộc gọi bị từ chối!");
            setIsOnCall(false);
            setCurrentCallId(null);
            setCurrentRoomName(null);
          } else if (callData.status === "ended") {
            setShowCall(false);
            setIsOnCall(false);
            setCurrentCallId(null);
            setCurrentRoomName(null);
          }
        }
      });
    });
    
    return () => {
      unsubscribeIncoming();
      unsubscribeOutgoing();
    };
  }, [userId, otherUserId]);

  // Auto scroll to bottom when new messages arrive
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  useEffect(() => {
    return () => {
      if (isOnCall && currentCallId) {
        try {
          updateDoc(doc(db, "videoCalls", currentCallId), {
            status: "ended",
            endedAt: new Date()
          });
        } catch (error) {
          // Silent cleanup error
        }
      }
    };
  }, []);

  useEffect(() => {
    if (user?.result?.id) {
      setUserId(user.result.id);
    }
    
    let otherUserIdFromParams = null;
    let otherUserFromParams = null;
    
    if (params?.userId && params.userId !== user?.result?.id) {
      otherUserIdFromParams = params.userId;
      otherUserFromParams = { id: otherUserIdFromParams };
    }
    
    let otherUserIdFromStorage = null;
    let otherUserFromStorage = null;
    try {
      const savedChatData = localStorage.getItem('lastChatUser');
      if (savedChatData) {
        const chatData = JSON.parse(savedChatData);
        if (chatData.userId && chatData.userId !== user?.result?.id) {
          otherUserIdFromStorage = chatData.userId;
          otherUserFromStorage = chatData;
        }
      }
    } catch (error) {
      // Ignore parse error
    }
    
    let other = null;
    let otherId = null;
    
    if (location.state?.room?.doctorId) {
      otherId = location.state.room.doctorId;
      other = location.state.room.doctor || location.state.room || { id: otherId };
    } else if (location.state?.room?.patientId) {
      otherId = location.state.room.patientId;
      other = location.state.room.patient || location.state.room || { id: otherId };
    } else if (location.state?.room?.userId1) {
      const currentUserId = user?.result?.id;
      if (location.state.room.userId1 === currentUserId) {
        otherId = location.state.room.userId2;
      } else {
        otherId = location.state.room.userId1;
      }
      other = { id: otherId };
    } else if (location.state?.appointment?.doctorId) {
      const currentUserId = user?.result?.id;
      if (currentUserId === location.state.appointment.doctorId) {
        otherId = location.state.appointment.patientId;
        other = location.state.appointment.patient || { 
          id: otherId,
          firstName: location.state.appointment.patientName?.split(' ')[0] || '',
          lastName: location.state.appointment.patientName?.split(' ').slice(1).join(' ') || ''
        };
      } else {
        otherId = location.state.appointment.doctorId;
        other = location.state.appointment.doctor || { 
          id: otherId,
          firstName: location.state.appointment.doctorName?.split(' ')[0] || '',
          lastName: location.state.appointment.doctorName?.split(' ').slice(1).join(' ') || ''
        };
      }
    } else if (location.state?.appointment?.patientId) {
      otherId = location.state.appointment.patientId;
      other = location.state.appointment.patient || { id: otherId };
    } else if (location.state?.slot?.doctorId) {
      otherId = location.state.slot.doctorId;
      other = location.state.slot.doctor || { id: otherId };
    } else if (location.state?.doctor) {
      other = location.state.doctor;
      otherId = other.doctorId || other.id || other.userId;
    } else if (location.state?.user) {
      other = location.state.user;
      otherId = other.id || other.userId;
    }
    
    if (!otherId) {
      if (otherUserIdFromParams) {
        otherId = otherUserIdFromParams;
        other = otherUserFromParams;
      } else if (otherUserIdFromStorage) {
        otherId = otherUserIdFromStorage;
        other = otherUserFromStorage;
      }
    }
    
    if (!otherId && location.state?.appointment) {
      const appointment = location.state.appointment;
      const currentUserId = user?.result?.id;
      
      if (appointment.doctorId === currentUserId && appointment.patientId) {
        otherId = appointment.patientId;
        other = appointment.patient || { id: otherId };
      } 
      else if (appointment.patientId === currentUserId && appointment.doctorId) {
        otherId = appointment.doctorId;
        other = appointment.doctor || { id: otherId };
      }
    }
    
    if (!otherId && location.state?.room) {
      const room = location.state.room;
      const currentUserId = user?.result?.id;
      
      if (room.doctorId === currentUserId && room.patientId) {
        otherId = room.patientId;
        other = room.patient || { id: otherId };
      } 
      else if (room.patientId === currentUserId && room.doctorId) {
        otherId = room.doctorId;
        other = room.doctor || { id: otherId };
      }
    }
    
    if (!otherId) {
      const currentUserId = user?.result?.id;
      const allData = { ...location.state?.appointment, ...location.state?.room, ...location.state };
      
      const possibleIds = [
        allData.doctorId,
        allData.patientId, 
        allData.userId,
        allData.id,
        allData.doctor?.id,
        allData.doctor?.doctorId,
        allData.patient?.id,
        allData.patient?.patientId,
        allData.user?.id
      ].filter(id => id && id !== currentUserId);
      
      if (possibleIds.length > 0) {
        otherId = possibleIds[0];
        other = { id: otherId };
      }
    }
    
    if (!otherId) {
      setOtherUserId(null);
      setShowUserSelector(true);
      return;
    }
    
    if (otherId && otherId !== user?.result?.id) {
      setOtherUserId(otherId);
      
      try {
        localStorage.setItem('lastChatUser', JSON.stringify({
          userId: otherId,
          firstName: other?.firstName || '',
          lastName: other?.lastName || '',
          avatar: other?.avatar || '/default-avatar.png'
        }));
      } catch (error) {
        // Ignore storage error
      }
      
      const avatar = other?.avatar || '/default-avatar.png';
      const firstName = other?.firstName || '';
      const lastName = other?.lastName || '';
      setOtherUser({ id: otherId, avatar, firstName, lastName });
      setOtherAvatar(avatar);
      setShowUserSelector(false);
    } else {
      setShowUserSelector(true);
    }
  }, [user, location.state]);

  useEffect(() => {
    setCurrentAvatar(user?.result?.avatar || '/default-avatar.png');
    setOtherAvatar((otherUser && otherUser.avatar) ? otherUser.avatar : '/default-avatar.png');
  }, [user, otherUser]);

  // Auto scroll to bottom when new messages arrive
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Removed videoCalls useEffect - not needed with Jitsi

  const handleSendMessage = async () => {
    try {
      if (!messageText.trim() && !imageFile) {
        return;
      }
      
      if (!otherUserId) {
        alert("Không thể gửi tin nhắn: Không xác định được người nhận!");
        return;
      }
      
      if (!userId) {
        alert("Không thể gửi tin nhắn: Không xác định được người gửi!");
        return;
      }
      
      if (imageFile) {
        setShowImageConfirmModal(true);
        return;
      }
      
      setLoading(true);
      
      const now = new Date();
      const messageData = {
        senderId: userId,
        receiverId: otherUserId,
        text: messageText.trim(),
        imageUrl: null,
        createdAt: now,
        isNew: true,
        timestamp: now.getTime(),
        serverTimestamp: now.toISOString()
      };
      
      const docRef = await addDoc(collection(db, "messages"), messageData);
      
      setTimeout(async () => {
        try {
          const checkQuery = query(
            collection(db, "messages"),
            orderBy("createdAt", "desc"),
            limit(1)
          );
          const snapshot = await getDocs(checkQuery);
        } catch (error) {
          // Silent check
        }
      }, 500);
      
      setMessageText("");
      setLoading(false);
      
      setTimeout(() => {
        if (messagesEndRef.current) {
          messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
      }, 100);
    } catch (error) {
      setLoading(false);
      alert("Gửi tin nhắn thất bại: " + error.message);
    }
  };

  const handleConfirmImageSend = async () => {
    try {
      setLoading(true);
      let imageUrl = null;
      
      if (imageFile) {
        const formData = new FormData();
        formData.append("file", imageFile);
        formData.append("upload_preset", CLOUDINARY_PRESET || "clinicapp");
        const res = await fetch(CLOUDINARY_URL, {
          method: "POST",
          body: formData,
        });
        const data = await res.json();
        imageUrl = data.secure_url;
      }
      
      const messageData = {
        senderId: userId,
        receiverId: otherUserId,
        text: messageText.trim(),
        imageUrl: imageUrl || null,
        createdAt: new Date(),
        isNew: true
      };
      
      const docRef = await addDoc(collection(db, "messages"), messageData);
      
      setMessageText("");
      setImageFile(null);
      setShowImageConfirmModal(false);
      setLoading(false);
    } catch (error) {
      setLoading(false);
      alert("Gửi tin nhắn thất bại: " + error.message);
    }
  };

  const handleCallRequest = async () => {
    if (!otherUserId) {
      alert("Không thể gọi video: Chưa xác định được người nhận!");
      return;
    }
    
    try {
      const roomName = `clinic_${userId?.substring(0, 8)}_${otherUserId?.substring(0, 8)}`;
      setCurrentRoomName(roomName);
      
      const callData = {
        senderId: userId,
        receiverId: otherUserId,
        senderName: `${user?.result?.firstName || ''} ${user?.result?.lastName || ''}`.trim() || 'User',
        roomName: roomName,
        status: "pending",
        createdAt: new Date(),
        timestamp: Date.now()
      };
      
      const docRef = await addDoc(collection(db, "videoCalls"), callData);
      setCurrentCallId(docRef.id);
      
      alert("📞 Đã gửi lời mời video call! Đang chờ người kia trả lời...");
      
    } catch (error) {
      alert("Lỗi khi gửi lời mời video call: " + error.message);
    }
  };

  const handleAcceptCall = async () => {
    if (!incomingCall) return;
    
    try {
      setCurrentRoomName(incomingCall.roomName);
      
      await updateDoc(doc(db, "videoCalls", incomingCall.id), {
        status: "accepted",
        acceptedAt: new Date()
      });
      
      setShowCall(true);
      setIsOnCall(true);
      setShowCallModal(false);
      setIncomingCall(null);
      setCurrentCallId(incomingCall.id);
      
    } catch (error) {
      alert("Lỗi khi nhận cuộc gọi: " + error.message);
    }
  };

  const handleRejectCall = async () => {
    if (!incomingCall) return;
    
    try {
      await updateDoc(doc(db, "videoCalls", incomingCall.id), {
        status: "rejected",
        rejectedAt: new Date()
      });
      
      setShowCallModal(false);
      setIncomingCall(null);
      
    } catch (error) {
      // Silent error
    }
  };

  const handleEndCallBothSides = async () => {
    try {
      if (currentCallId) {
        await updateDoc(doc(db, "videoCalls", currentCallId), {
          status: "ended",
          endedAt: new Date()
        });
      }
      
      setShowCall(false);
      setIsOnCall(false);
      setCurrentCallId(null);
      setCurrentRoomName(null);
      setIncomingCall(null);
      
    } catch (error) {
      setShowCall(false);
      setIsOnCall(false);
      setCurrentCallId(null);
      setCurrentRoomName(null);
      setIncomingCall(null);
    }
  };

  const handleLeaveCallUI = () => {
    setShowCall(false);
    setIsOnCall(false);
  };

  const handleGoBack = () => {
    // Quay lại trang trước đó hoặc trang chính
    if (window.history.length > 1) {
      navigate(-1); // Quay lại trang trước
    } else {
      navigate('/'); // Hoặc về trang chủ
    }
  };
  
  const handleManualUserSelection = () => {
    if (!tempOtherUserId.trim()) {
      alert("Vui lòng nhập ID người nhận!");
      return;
    }
    
    if (tempOtherUserId.trim() === user?.result?.id) {
      alert("Không thể chat với chính mình!");
      return;
    }
    
    const otherId = tempOtherUserId.trim();
    
    setOtherUserId(otherId);
    setOtherUser({ 
      id: otherId, 
      firstName: 'User', 
      lastName: otherId.substring(0, 8),
      avatar: '/default-avatar.png' 
    });
    setOtherAvatar('/default-avatar.png');
    setShowUserSelector(false);
    
    try {
      localStorage.setItem('lastChatUser', JSON.stringify({
        userId: otherId,
        firstName: 'User',
        lastName: otherId.substring(0, 8),
        avatar: '/default-avatar.png'
      }));
    } catch (error) {
      // Ignore storage error
    }
  };

  return (
    <Container fluid className="chat-container">
      {showCall ? (
        // Khi đang gọi video - che hết phần chat
        <div className="video-call-fullscreen">
          <div className="d-flex justify-content-between align-items-center mb-3 p-3 bg-light rounded">
            <h5 className="mb-0">
              <i className="bi bi-camera-video-fill me-2"></i>
              Video Call với {`${otherUser?.firstName || ''} ${otherUser?.lastName || ''}`.trim() || 'Người dùng'}
            </h5>
            <Button 
              variant="outline-secondary" 
              size="sm"
              onClick={handleLeaveCallUI}
              title={t('roomChat.back')}
            >
              <i className="bi bi-arrow-left me-2"></i>
              {t('roomChat.back')}
            </Button>
          </div>
          <JitsiCall 
            roomName={currentRoomName || `clinic_${userId?.substring(0, 8)}_${otherUserId?.substring(0, 8)}`}
            displayName={`${user?.result?.firstName || ''} ${user?.result?.lastName || ''}`.trim() || 'User'}
            onEndCall={handleEndCallBothSides}
          />
        </div>
      ) : (
        // Giao diện chat bình thường
        <>
          <Row className="chat-header">
            <Col>
              <div className="chat-header-content">
                <Button 
                  variant="outline-secondary" 
                  size="sm" 
                  className="back-btn me-3"
                  onClick={handleGoBack}
                  title={t('roomChat.back')}
                >
                  <i className="bi bi-arrow-left"></i> {t('roomChat.back')}
                </Button>
                <MemoizedAvatar
                  src={memoizedOtherAvatar}
                  alt="Avatar"
                  className="header-avatar"
                  onLoad={() => setAvatarLoaded(prev => ({ ...prev, other: true }))}
                  onError={() => setAvatarLoaded(prev => ({ ...prev, other: false }))}
                />
                <h5 className="header-title">
                  {`${otherUser?.firstName || ''} ${otherUser?.lastName || ''}`.trim() || 'Người dùng'}
                  {userId && otherUserId && (
                    <small className="online-status">
                      <i className="bi bi-circle-fill online-dot"></i> Online
                    </small>
                  )}
                </h5>
              </div>
              <div className="d-flex justify-content-end gap-2">
                <Button 
                  onClick={handleCallRequest} 
                  className="call-btn" 
                  variant={isOnCall ? "warning" : "success"}
                  disabled={!otherUserId}
                >
                  <i className={`bi ${isOnCall ? "bi-camera-video-off" : "bi-telephone-fill"} me-2`}></i>
                  {isOnCall ? t('roomChat.callVideo') : t('roomChat.callVideo')}
                </Button>
              </div>
            </Col>
          </Row>

          <Row className="chat-box">
            <Col className="chat-messages">
              {!userId || !otherUserId ? (
                <div className="text-center text-muted py-4">
                  <div className="spinner-border" role="status">
                    <span className="visually-hidden">{t('roomChat.loading')}</span>
                  </div>
                  <p className="mt-2">{t('roomChat.loading')}</p>
                </div>
              ) : messages.length === 0 ? (
                <div className="text-center text-muted py-4">
                  <i className="bi bi-chat-dots" style={{fontSize: 48}}></i>
                  <p className="mt-2">{t('roomChat.message')}</p>
                </div>
              ) : (
                renderedMessages
              )}
              <div ref={messagesEndRef}></div>
            </Col>
          </Row>

          <Row className="input-row align-items-center py-2">
            <Col xs={12}>
              <div className="d-flex align-items-center flex-wrap">
                <div className="d-flex align-items-center flex-grow-1">
                  <label htmlFor="image-upload" style={{ cursor: "pointer", marginBottom: 0, marginRight: 10 }}>
                    <i className="bi bi-image" style={{ fontSize: 24, color: "#0d6efd" }} title={t('roomChat.sendImage')}></i>
                  </label>
                  <input
                    id="image-upload"
                    type="file"
                    accept="image/*"
                    style={{ display: "none" }}
                    onChange={e => setImageFile(e.target.files[0])}
                  />
                  <Form.Control
                    as="textarea"
                    rows={1}
                    placeholder={t('roomChat.sendMessage')}
                    value={messageText}
                    onChange={e => setMessageText(e.target.value)}
                    className="message-input"
                    style={{ borderRadius: 20, paddingLeft: 16, paddingRight: 40, resize: "none", flex: 1 }}
                    autoFocus
                    onKeyDown={e => {
                      if (e.key === "Enter" && !e.shiftKey && (messageText.trim() || imageFile)) {
                        e.preventDefault();
                        if (imageFile) {
                          setShowImageConfirmModal(true);
                        } else {
                          handleSendMessage();
                        }
                      }
                    }}
                  />
                </div>
                {imageFile && (
                  <div className="mt-2" style={{ position: "relative", zIndex: 10 }}>
                    <img
                      src={URL.createObjectURL(imageFile)}
                      alt="Preview"
                      style={{ maxWidth: 120, maxHeight: 120, borderRadius: 8, objectFit: "contain", border: "1px solid #ddd", background: "#fff" }}
                    />
                    <span style={{
                      position: "absolute",
                      top: -8,
                      right: -8,
                      background: "#dc3545",
                      color: "#fff",
                      borderRadius: "50%",
                      fontSize: 12,
                      width: 18,
                      height: 18,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      cursor: "pointer",
                      zIndex: 11
                    }}
                      title="Xóa ảnh"
                      onClick={() => setImageFile(null)}
                    >×</span>
                  </div>
                )}
                <Button
                  onClick={handleSendMessage}
                  disabled={loading || (!messageText.trim() && !imageFile)}
                  className="send-btn d-flex align-items-center justify-content-center mt-2"
                  style={{ borderRadius: "50%", width: 40, height: 40, fontSize: 20 }}
                  variant="primary"
                  title={t('roomChat.sendMessage')}
                >
                  {loading ? (
                    <div className="spinner-border spinner-border-sm" role="status" style={{width: 16, height: 16}}>
                      <span className="visually-hidden">{t('roomChat.loading')}</span>
                    </div>
                  ) : (
                    <i className="bi bi-send"></i>
                  )}
                </Button>
              </div>
            </Col>
          </Row>
        </>
      )}

      {/* Modal cho user chọn otherUserId khi không tìm thấy */}
      {showUserSelector && (
        <Modal show centered backdrop="static" keyboard={false}>
          <Modal.Header>
            <Modal.Title>⚠️ {t('roomChat.warning')}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <div className="text-center mb-3">
              <i className="bi bi-person-question" style={{fontSize: 48, color: '#ffc107'}}></i>
              <h5 className="mt-2">{t('roomChat.error')}</h5>
              <p className="text-muted">
                {t('roomChat.info')}
              </p>
            </div>
            
            <Form.Group className="mb-3">
              <Form.Label>{t('roomChat.other')}:</Form.Label>
              <Form.Control
                type="text"
                placeholder="Nhập ID người dùng (ví dụ: 5c27273e-87be-11f0-abcd-005056c00002)"
                value={tempOtherUserId}
                onChange={(e) => setTempOtherUserId(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    handleManualUserSelection();
                  }
                }}
              />
              <Form.Text className="text-muted">
                💡 {t('roomChat.info')}
              </Form.Text>
            </Form.Group>
            
            <div className="d-flex justify-content-between gap-2">
              <Button variant="secondary" onClick={handleGoBack}>
                <i className="bi bi-arrow-left me-2"></i>
                {t('roomChat.cancel')}
              </Button>
              <Button 
                variant="primary" 
                onClick={handleManualUserSelection}
                disabled={!tempOtherUserId.trim()}
              >
                <i className="bi bi-check-lg me-2"></i>
                {t('roomChat.confirm')}
              </Button>
            </div>
          </Modal.Body>
        </Modal>
      )}

      {showImageConfirmModal && (
        <Modal show centered onHide={() => setShowImageConfirmModal(false)}>
          <Modal.Header closeButton>
            <Modal.Title>{t('roomChat.confirm')}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>{t('roomChat.sendImage')}</p>
            <img
              src={URL.createObjectURL(imageFile)}
              alt="Preview"
              style={{ maxWidth: "100%", maxHeight: 300, borderRadius: 8, objectFit: "contain" }}
            />
            <div className="d-flex justify-content-end gap-2 mt-3">
              <Button variant="secondary" onClick={() => setShowImageConfirmModal(false)}>
                {t('roomChat.cancel')}
              </Button>
              <Button variant="primary" onClick={handleConfirmImageSend} disabled={loading}>
                {t('roomChat.sendMessage')}
              </Button>
            </div>
          </Modal.Body>
        </Modal>
      )}

      {/* Incoming call modal */}
      {showCallModal && incomingCall && (
        <Modal show centered backdrop="static" keyboard={false}>
          <Modal.Header>
            <Modal.Title className="d-flex align-items-center">
              <i className="bi bi-camera-video-fill me-2 text-success"></i>
              {t('roomChat.callIncoming')}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body className="text-center">
            <div className="mb-3">
              <i className="bi bi-person-circle" style={{fontSize: 60, color: '#007bff'}}></i>
            </div>
            <h5 className="mb-2">{incomingCall.senderName}</h5>
            <p className="text-muted mb-4">{t('roomChat.call')}</p>
            
            <div className="d-flex justify-content-center gap-3">
              <Button 
                variant="danger" 
                size="lg"
                onClick={handleRejectCall}
                className="px-4"
              >
                <i className="bi bi-telephone-x-fill me-2"></i>
                {t('roomChat.declineCall')}
              </Button>
              <Button 
                variant="success" 
                size="lg"
                onClick={handleAcceptCall}
                className="px-4"
              >
                <i className="bi bi-camera-video-fill me-2"></i>
                {t('roomChat.acceptCall')}
              </Button>
            </div>
            
            <div className="mt-3">
              <small className="text-muted">{t('roomChat.room')}: {incomingCall.roomName}</small>
            </div>
          </Modal.Body>
        </Modal>
      )}
    </Container>
  );
};

export default RoomChat;