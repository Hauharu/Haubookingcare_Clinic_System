import React, { useEffect, useState, useRef } from 'react';
import { Alert, Button } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';

const JitsiCall = ({ roomName, displayName, onEndCall }) => {
  const [permissionError, setPermissionError] = useState(false);
  const [jitsiLoaded, setJitsiLoaded] = useState(false);
  const [isRequestingPermission, setIsRequestingPermission] = useState(false);
  const [iframeSrc, setIframeSrc] = useState('');
  const hasLoadedRef = useRef(false);
  const { t } = useTranslation();

  useEffect(() => {
    if (hasLoadedRef.current || !roomName || !displayName) {
      return;
    }
    
    hasLoadedRef.current = true;
    setPermissionError(false);
    setIsRequestingPermission(true);
    
    try {
      // Truyền tên người dùng qua URL parameter
      const encodedName = encodeURIComponent(displayName);
      const jitsiUrl = `https://meet.jit.si/${roomName}#userInfo.displayName="${encodedName}"`;
      setIframeSrc(jitsiUrl);
      
      setTimeout(() => {
        setJitsiLoaded(true);
        setIsRequestingPermission(false);
      }, 1000);
      
    } catch (error) {
      setPermissionError(true);
      setIsRequestingPermission(false);
    }

    return () => {
      hasLoadedRef.current = false;
      setIframeSrc('');
      setJitsiLoaded(false);
      setIsRequestingPermission(false);
    };
  }, [roomName, displayName]);

  const handleOpenVideoCall = () => {
    if (iframeSrc) {
      const windowFeatures = 'width=1200,height=800,menubar=no,toolbar=no,location=no,status=no,resizable=yes,scrollbars=yes';
      const newWindow = window.open(iframeSrc, 'JitsiMeet', windowFeatures);
      
      if (newWindow) {
        newWindow.focus();
      } else {
        alert(t('jitsiCall.popupRequired'));
      }
    }
  };

  const handleEndCall = () => {
    setIframeSrc('');
    setJitsiLoaded(false);
    hasLoadedRef.current = false;
    
    if (onEndCall) {
      onEndCall();
    }
  };

  return (
    <div>
      {isRequestingPermission && !permissionError && (
        <Alert variant="info" className="mb-3">
          <Alert.Heading>🎥 {t('jitsiCall.initializing')}</Alert.Heading>
          <p>{t('jitsiCall.waiting')}</p>
          <div className="d-flex align-items-center">
            <div className="spinner-border spinner-border-sm text-info me-2" role="status">
              <span className="visually-hidden">{t('jitsiCall.loading')}</span>
            </div>
            <span>{t('jitsiCall.loadingJitsi')}</span>
          </div>
        </Alert>
      )}
      
      {permissionError && (
        <Alert variant="danger" className="mb-3">
          <Alert.Heading>🚨 {t('jitsiCall.errorInit')}</Alert.Heading>
          <p>{t('jitsiCall.fail')}</p>
          <div className="mt-3">
            <Button 
              variant="primary" 
              onClick={() => window.location.reload()}
              className="me-2"
            >
              🔄 {t('jitsiCall.tryAgain')}
            </Button>
          </div>
        </Alert>
      )}
      
      {jitsiLoaded && (
        <div className="d-flex justify-content-center mb-3">
          <Button 
            variant="success" 
            size="lg"
            onClick={handleOpenVideoCall}
            className="px-4 me-3"
          >
            🎥 {t('jitsiCall.joinCall')}
          </Button>
          <Button 
            variant="danger" 
            size="lg"
            onClick={handleEndCall}
            className="px-4"
          >
            📞 {t('jitsiCall.endCall')}
          </Button>
        </div>
      )}
      
      <div 
        id="jitsi-container" 
        style={{ 
          width: '100%', 
          height: '400px',
          border: '1px solid #ddd',
          borderRadius: '8px',
          overflow: 'hidden',
          backgroundColor: '#f8f9fa'
        }}
      >
        {jitsiLoaded && (
          <div className="d-flex align-items-center justify-content-center h-100">
            <div className="text-center">
              <div style={{ fontSize: '4rem', color: '#198754' }}>🎥</div>
              <h4 className="mt-3 mb-2">{t('jitsiCall.callReady')}</h4>
              <p className="text-muted">{t('jitsiCall.room')}: <strong>{roomName}</strong></p>
              <div className="alert alert-success mt-3 mb-3" style={{ fontSize: '0.9rem' }}>
                <i className="bi bi-check-circle me-2"></i>
                <strong>{t('jitsiCall.displayName')}:</strong> {displayName}
                <br />
              </div>
              <Button 
                variant="outline-success" 
                size="lg"
                onClick={handleOpenVideoCall}
                className="mt-2"
              >
                🚀 {t('jitsiCall.openCall')}
              </Button>
            </div>
          </div>
        )}
        
        {!jitsiLoaded && !permissionError && !isRequestingPermission && (
          <div className="d-flex align-items-center justify-content-center h-100">
            <div className="text-center">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">{t('jitsiCall.loading')}</span>
              </div>
              <p className="mt-3">{t('jitsiCall.initializing')}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default JitsiCall;
