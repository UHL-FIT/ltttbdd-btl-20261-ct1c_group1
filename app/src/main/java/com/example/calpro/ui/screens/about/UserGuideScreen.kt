package com.example.calpro.ui.screens.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserGuideScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Hướng dẫn sử dụng CalcPro X",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Chào mừng bạn đến với CalcPro - Ứng dụng máy tính khoa học và công cụ chuyển đổi thông minh. Dưới đây là các thao tác cơ bản để bạn làm chủ ứng dụng một cách nhanh chóng:",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        GuideSection(
            title = "1. \uD83E\uDDEE Màn hình Máy tính (Calculator)",
            content = "Nhập biểu thức: Chạm vào các phím số và toán tử để nhập biểu thức. Bạn có thể sử dụng dấu ngoặc ( ) để ưu tiên các phép tính phức tạp.\n\n" +
                    "Hàm khoa học: Hỗ trợ tính toán lượng giác (sin, cos, tan), logarit (log, ln), căn bậc hai (√) và lũy thừa (x²). Các hàm lượng giác mặc định sử dụng đơn vị Radian.\n\n" +
                    "Xóa dữ liệu: * Nhấn DEL để xóa từng ký tự vừa nhập. Nhấn AC để xóa toàn bộ biểu thức trên màn hình.\n\n" +
                    "Kết quả tức thời: Kết quả tạm tính sẽ hiển thị mờ ngay bên dưới biểu thức khi bạn đang nhập. Nhấn phím = để chốt kết quả và tự động lưu vào Lịch sử."
        )

        Spacer(modifier = Modifier.height(16.dp))

        GuideSection(
            title = "2. \uD83D\uDD52 Quản lý Lịch sử (History)",
            content = "Lưu trữ tự động: Mọi phép tính sau khi nhấn dấu = đều được lưu tự động. Ứng dụng giữ lại tối đa 50 phép tính gần nhất để tối ưu bộ nhớ.\n\n" +
                    "Tái sử dụng kết quả: Chạm vào bất kỳ một mục nào trong danh sách lịch sử, biểu thức hoặc kết quả đó sẽ lập tức được đưa ngược trở lại màn hình Máy tính để bạn tính toán tiếp.\n\n" +
                    "Xóa lịch sử: Đánh dấu tick vào ô vuông (Checkbox) bên cạnh các phép tính bạn muốn xóa, sau đó nhấn biểu tượng Thùng rác. Bạn cũng có thể dùng nút Chọn tất cả để dọn dẹp nhanh chóng."
        )

        Spacer(modifier = Modifier.height(16.dp))

        GuideSection(
            title = "3. \uD83D\uDD04 Chuyển đổi đơn vị (Converter)",
            content = "Đổi đơn vị: Chọn danh mục cần chuyển đổi (Chiều dài, Khối lượng, Nhiệt độ). Nhập giá trị vào ô trên, ô dưới sẽ tự động nhảy kết quả và ngược lại (Chuyển đổi 2 chiều)."
        )

        Spacer(modifier = Modifier.height(16.dp))

        GuideSection(
            title = "4. \uD83C\uDFA8 Cài đặt & Giao diện",
            content = "Mở thanh Menu (biểu tượng 3 gạch ngang) ở góc trái màn hình, chọn mục Cài đặt.\n\n" +
                    "Tại đây, bạn có thể chuyển đổi linh hoạt giữa giao diện Sáng (Light Mode) và Tối (Dark Mode), hoặc chọn chế độ Hệ thống để ứng dụng tự động đồng bộ theo cài đặt của điện thoại."
        )
    }
}

@Composable
fun GuideSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun UserGuideScreenPreview() {
    com.example.calpro.ui.theme.CalcProTheme {
        androidx.compose.material3.Surface(color = MaterialTheme.colorScheme.background) {
            UserGuideScreen()
        }
    }
}