// VNPay Configuration cho Frontend
// Chỉ chứa thông tin công khai, không chứa secret key

const VNPayConfig = {
    // Thông tin công khai
    version: "2.1.0",
    command: "pay",
    currency: "VND",
    orderType: "other",
    locale: "vn",
    
    // URLs (sẽ được backend xử lý)
    paymentUrl: "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html", // Chỉ để hiển thị
    
    // Frontend return URLs (nơi VNPay sẽ redirect sau khi thanh toán)
    // Backend sẽ xử lý callback và redirect về những URL này
    frontendReturnUrls: {
        success: `${window.location.origin}/payment/success?status=success`,
        failed: `${window.location.origin}/payment/failed?status=failed`,
        invalid: `${window.location.origin}/payment/invalid?status=invalid`
    },
    
    // Thông tin hiển thị
    displayName: "VNPay",
    description: "Thanh toán online qua VNPay",
    
    // Validation
    minAmount: 1000, // 1,000 VND
    maxAmount: 500000000, // 500,000,000 VND
    
    // Format số tiền cho VNPay (VNPay yêu cầu x100)
    formatAmount: (amount) => {
        return Math.round(amount * 100);
    },
    
    // Kiểm tra amount hợp lệ
    validateAmount: (amount) => {
        return amount >= VNPayConfig.minAmount && amount <= VNPayConfig.maxAmount;
    },
    
    // Format order info (remove Vietnamese chars)
    formatOrderInfo: (info) => {
        if (!info) return "Thanh toan dich vu y te";
        
        return info
            .replace(/[àáạảãâầấậẩẫăằắặẳẵ]/g, "a")
            .replace(/[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]/g, "A")
            .replace(/[èéẹẻẽêềếệểễ]/g, "e")
            .replace(/[ÈÉẸẺẼÊỀẾỆỂỄ]/g, "E")
            .replace(/[ìíịỉĩ]/g, "i")
            .replace(/[ÌÍỊỈĨ]/g, "I")
            .replace(/[òóọỏõôồốộổỗơờớợởỡ]/g, "o")
            .replace(/[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]/g, "O")
            .replace(/[ùúụủũưừứựửữ]/g, "u")
            .replace(/[ÙÚỤỦŨƯỪỨỰỬỮ]/g, "U")
            .replace(/[ỳýỵỷỹ]/g, "y")
            .replace(/[ỲÝỴỶỸ]/g, "Y")
            .replace(/đ/g, "d")
            .replace(/Đ/g, "D")
            .replace(/[^a-zA-Z0-9\s\-_.,]/g, "")
            .substring(0, 100); // Giới hạn 100 ký tự
    }
};

export default VNPayConfig;
