package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.entity.User;
import com.trunghau1510.Clinic_Management.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImplement implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendDoctorAccountCreatedEmail(User user, String tempUsername, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("trunghauu71@gmail.com", "Phòng khám đa khoa tư nhân OUBookingCare");
            helper.setTo(user.getEmail());
            helper.setSubject("[OUBookingCare] Thông báo tạo tài khoản bác sĩ thành công");
            String htmlContent = String.format("""
                <html>
                  <body style='font-family: Arial, sans-serif; line-height: 1.7; color: #222;'>
                    <div style='border-bottom:2px solid #009688; margin-bottom:16px;'>
                      <h2 style='color:#009688;'>Phòng khám đa khoa tư nhân OUBookingCare</h2>
                    </div>
                    <p>Kính gửi Bác sĩ <strong>%s %s</strong>,</p>
                    <p>Chúng tôi xin trân trọng thông báo tài khoản bác sĩ của bạn đã được tạo thành công trên hệ thống OUBookingCare.</p>
                    <table style='border-collapse:collapse;'>
                      <tr><td><strong>Tên đăng nhập:</strong></td><td>%s</td></tr>
                      <tr><td><strong>Mật khẩu tạm thời:</strong></td><td>%s</td></tr>
                    </table>
                    <p>Vui lòng cung cấp chứng chỉ hành nghề và đổi mật khẩu sau khi đăng nhập lần đầu để đảm bảo bảo mật.</p>
                    <p>Chân thành cảm ơn Quý bác sĩ đã đồng hành cùng phòng khám!</p>
                    <br>
                    <div style='border-top:1px solid #eee; margin-top:24px; padding-top:12px;'>
                      <strong>Phòng khám đa khoa tư nhân OUBookingCare</strong><br>
                      Địa chỉ: Khu dân cư Nhơn Đức, xã Hiệp Phước, TP. Hồ Chí Minh<br>
                      Điện thoại: 0123 456 789<br>
                      Website: <a href='https://oubookingcare.vn'>oubookingcare.vn</a>
                    </div>
                  </body>
                </html>
            """, user.getFirstName(), user.getLastName(), tempUsername, tempPassword);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Không thể gửi email chấp nhận bác sĩ", e);
        }
    }

    @Override
    public void sendDoctorApprovalEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("trunghauu71@gmail.com", "Phòng khám đa khoa tư nhân OUBookingCare");
            helper.setTo(user.getEmail());
            helper.setSubject("[OUBookingCare] Thông báo duyệt chứng chỉ hành nghề bác sĩ");
            String htmlContent = String.format("""
                <html>
                  <body style='font-family: Arial, sans-serif; line-height: 1.7; color: #222;'>
                    <div style='border-bottom:2px solid #009688; margin-bottom:16px;'>
                      <h2 style='color:#009688;'>Phòng khám đa khoa tư nhân OUBookingCare</h2>
                    </div>
                    <p>Kính gửi Bác sĩ <strong>%s %s</strong>,</p>
                    <p>Chúng tôi xin trân trọng thông báo chứng chỉ hành nghề của bạn đã được <strong>duyệt thành công</strong> trên hệ thống OUBookingCare.</p>
                    <p>Bạn đã chính thức trở thành bác sĩ của phòng khám và có thể bắt đầu nhận lịch khám.</p>
                    <p>Chân thành cảm ơn Quý bác sĩ đã đồng hành cùng phòng khám!</p>
                    <br>
                    <div style='border-top:1px solid #eee; margin-top:24px; padding-top:12px;'>
                      <strong>Phòng khám đa khoa tư nhân OUBookingCare</strong><br>
                      Địa chỉ: Khu dân cư Nhơn Đức, xã Hiệp Phước, TP. Hồ Chí Minh<br>
                      Điện thoại: 0123 456 789<br>
                      Website: <a href='https://oubookingcare.vn'>oubookingcare.vn</a>
                    </div>
                  </body>
                </html>
            """, user.getFirstName(), user.getLastName());
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Không thể gửi email duyệt chứng chỉ bác sĩ", e);
        }
    }

    @Override
    public void sendDoctorRejectionEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("trunghauu71@gmail.com", "Phòng khám đa khoa tư nhân OUBookingCare");
            helper.setTo(user.getEmail());
            helper.setSubject("[OUBookingCare] Thông báo từ chối chứng chỉ hành nghề bác sĩ");
            String htmlContent = String.format("""
                <html>
                  <body style='font-family: Arial, sans-serif; line-height: 1.7; color: #222;'>
                    <div style='border-bottom:2px solid #009688; margin-bottom:16px;'>
                      <h2 style='color:#009688;'>Phòng khám đa khoa tư nhân OUBookingCare</h2>
                    </div>
                    <p>Kính gửi Bác sĩ <strong>%s %s</strong>,</p>
                    <p>Chúng tôi rất tiếc phải thông báo rằng <strong>chứng chỉ hành nghề bác sĩ</strong> của bạn <strong>không được duyệt</strong> trên hệ thống OUBookingCare.</p>
                    <p>Nếu cần hỗ trợ hoặc muốn nộp lại hồ sơ, vui lòng liên hệ với chúng tôi.</p>
                    <br>
                    <div style='border-top:1px solid #eee; margin-top:24px; padding-top:12px;'>
                      <strong>Phòng khám đa khoa tư nhân OUBookingCare</strong><br>
                      Địa chỉ: Khu dân cư Nhơn Đức, xã Hiệp Phước, TP. Hồ Chí Minh<br>
                      Điện thoại: 0123 456 789<br>
                      Website: <a href='https://oubookingcare.vn'>oubookingcare.vn</a>
                    </div>
                  </body>
                </html>
            """, user.getFirstName(), user.getLastName());
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Không thể gửi email từ chối chứng chỉ bác sĩ", e);
        }
    }

    @Override
    public void sendPaymentSuccessEmail(String toEmail, String patientName, String amount, String transactionId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("trunghauu71@gmail.com", "Phòng khám đa khoa tư nhân OUBookingCare");
            helper.setTo(toEmail);
            helper.setSubject("[OUBookingCare] Xác nhận thanh toán thành công");
            String htmlContent = String.format("""
                <html>
                  <body style='font-family: Arial, sans-serif; line-height: 1.7; color: #222;'>
                    <div style='border-bottom:2px solid #009688; margin-bottom:16px;'>
                      <h2 style='color:#009688;'>Phòng khám đa khoa tư nhân OUBookingCare</h2>
                    </div>
                    <p>Kính gửi Quý khách <strong>%s</strong>,</p>
                    <p>Chúng tôi xác nhận Quý khách đã thanh toán thành công với thông tin sau:</p>
                    <table style='border-collapse:collapse;'>
                      <tr><td><strong>Mã giao dịch:</strong></td><td>%s</td></tr>
                      <tr><td><strong>Số tiền:</strong></td><td>%s VNĐ</td></tr>
                    </table>
                    <p>Cảm ơn Quý khách đã tin tưởng và sử dụng dịch vụ của phòng khám!</p>
                    <br>
                    <div style='border-top:1px solid #eee; margin-top:24px; padding-top:12px;'>
                      <strong>Phòng khám đa khoa tư nhân OUBookingCare</strong><br>
                      Địa chỉ: Khu dân cư Nhơn Đức, xã Hiệp Phước, TP. Hồ Chí Minh<br>
                      Điện thoại: 0123 456 789<br>
                      Website: <a href='https://oubookingcare.vn'>oubookingcare.vn</a>
                    </div>
                  </body>
                </html>
            """, patientName, transactionId, amount);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Không thể gửi email xác nhận thanh toán", e);
        }
    }

    @Override
    public void sendAppointmentConfirmation(String toEmail, String subject, String patientName, String doctorName, String time) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("trunghauu71@gmail.com", "Phòng khám đa khoa tư nhân OUBookingCare");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            String htmlContent = String.format("""
                <html>
                  <body style='font-family: Arial, sans-serif; line-height: 1.7; color: #222;'>
                    <div style='border-bottom:2px solid #009688; margin-bottom:16px;'>
                      <h2 style='color:#009688;'>Phòng khám đa khoa tư nhân OUBookingCare</h2>
                    </div>
                    <p>Kính gửi Quý khách <strong>%s</strong>,</p>
                    <p>Quý khách đã đặt lịch khám với bác sĩ <strong>%s</strong> vào lúc <strong>%s</strong>.</p>
                    <p>Vui lòng đến đúng giờ để được phục vụ tốt nhất.</p>
                    <p>Cảm ơn Quý khách đã tin tưởng phòng khám!</p>
                    <br>
                    <div style='border-top:1px solid #eee; margin-top:24px; padding-top:12px;'>
                      <strong>Phòng khám đa khoa tư nhân OUBookingCare</strong><br>
                      Địa chỉ: Khu dân cư Nhơn Đức, xã Hiệp Phước, TP. Hồ Chí Minh<br>
                      Điện thoại: 0123 456 789<br>
                      Website: <a href='https://oubookingcare.vn'>oubookingcare.vn</a>
                    </div>
                  </body>
                </html>
            """, patientName, doctorName, time);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Không thể gửi email xác nhận lịch hẹn", e);
        }
    }

    @Override
    public void sendAppointmentConfirmationtoDoctor(String toEmailDoctor, String subject, String patientName, String doctorName, String time) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("trunghauu71@gmail.com", "Phòng khám đa khoa tư nhân OUBookingCare");
            helper.setTo(toEmailDoctor);
            helper.setSubject(subject);
            String htmlContent = String.format("""
                <html>
                  <body style='font-family: Arial, sans-serif; line-height: 1.7; color: #222;'>
                    <div style='border-bottom:2px solid #009688; margin-bottom:16px;'>
                      <h2 style='color:#009688;'>Phòng khám đa khoa tư nhân OUBookingCare</h2>
                    </div>
                    <p>Kính gửi Bác sĩ <strong>%s</strong>,</p>
                    <p>Bệnh nhân <strong>%s</strong> đã đặt lịch khám với Quý bác sĩ vào lúc <strong>%s</strong>.</p>
                    <p>Vui lòng kiểm tra lại thông tin và chuẩn bị tốt cho buổi khám.</p>
                    <br>
                    <div style='border-top:1px solid #eee; margin-top:24px; padding-top:12px;'>
                      <strong>Phòng khám đa khoa tư nhân OUBookingCare</strong><br>
                      Địa chỉ: Khu dân cư Nhơn Đức, xã Hiệp Phước, TP. Hồ Chí Minh<br>
                      Điện thoại: 0123 456 789<br>
                      Website: <a href='https://oubookingcare.vn'>oubookingcare.vn</a>
                    </div>
                  </body>
                </html>
            """, doctorName, patientName, time);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Không thể gửi email xác nhận lịch hẹn cho bác sĩ", e);
        }
    }

    @Override
    public void sendAppointmentCancellation(String toEmail, String subject, String patientName, String doctorName, String time) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("trunghauu71@gmail.com", "Phòng khám đa khoa tư nhân OUBookingCare");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            String htmlContent = String.format("""
                <html>
                  <body style='font-family: Arial, sans-serif; line-height: 1.7; color: #222;'>
                    <div style='border-bottom:2px solid #009688; margin-bottom:16px;'>
                      <h2 style='color:#009688;'>Phòng khám đa khoa tư nhân OUBookingCare</h2>
                    </div>
                    <p>Kính gửi Quý khách <strong>%s</strong>,</p>
                    <p>Lịch khám với bác sĩ <strong>%s</strong> vào lúc <strong>%s</strong> đã bị hủy.</p>
                    <p>Nếu cần hỗ trợ hoặc muốn đặt lại lịch, vui lòng liên hệ với phòng khám.</p>
                    <br>
                    <div style='border-top:1px solid #eee; margin-top:24px; padding-top:12px;'>
                      <strong>Phòng khám đa khoa tư nhân OUBookingCare</strong><br>
                      Địa chỉ: Khu dân cư Nhơn Đức, xã Hiệp Phước, TP. Hồ Chí Minh<br>
                      Điện thoại: 0123 456 789<br>
                      Website: <a href='https:/oubookingcare.vn'>oubookingcare.vn</a>
                    </div>
                  </body>
                </html>
            """, patientName, doctorName, time);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Không thể gửi email hủy lịch khám", e);
        }
    }

    @Override
    public void sendAppointmentCancellationToDoctor(String toEmailDoctor, String subject, String patientName, String doctorName, String time) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("trunghauu71@gmail.com", "Phòng khám đa khoa tư nhân OUBookingCare");
            helper.setTo(toEmailDoctor);
            helper.setSubject(subject);
            String htmlContent = String.format("""
                <html>
                  <body style='font-family: Arial, sans-serif; line-height: 1.7; color: #222;'>
                    <div style='border-bottom:2px solid #009688; margin-bottom:16px;'>
                      <h2 style='color:#009688;'>Phòng khám đa khoa tư nhân OUBookingCare</h2>
                    </div>
                    <p>Kính gửi Bác sĩ <strong>%s</strong>,</p>
                    <p>Bệnh nhân <strong>%s</strong> đã hủy lịch khám vào lúc <strong>%s</strong>.</p>
                    <p>Vui lòng kiểm tra lại lịch làm việc của mình.</p>
                    <br>
                    <div style='border-top:1px solid #eee; margin-top:24px; padding-top:12px;'>
                      <strong>Phòng khám đa khoa tư nhân OUBookingCare</strong><br>
                      Địa chỉ: Khu dân cư Nhơn Đức, xã Hiệp Phước, TP. Hồ Chí Minh<br>
                      Điện thoại: 0123 456 789<br>
                      Website: <a href='https://oubookingcare.vn'>oubookingcare.vn</a>
                    </div>
                  </body>
                </html>
            """, doctorName, patientName, time);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Không thể gửi email hủy lịch khám cho bác sĩ", e);
        }
    }
}
