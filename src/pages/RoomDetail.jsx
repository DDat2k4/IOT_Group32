import SensorCard from "../components/SensorCard";

// Giả lập dữ liệu các phòng
const roomsData = {
  101: {
    devices: [
      {
        name: "Thiết bị 101-A",
        status: "Online",
        sensors: [
          { name: "Nhiệt độ", status: "Bình thường", threshold: "50°C" },
          { name: "Khói", status: "Cảnh báo", threshold: "5 ppm" },
        ],
      },
    ],
  },
  202: {
    devices: [
      {
        name: "Thiết bị 202-B",
        status: "Offline",
        sensors: [ 
          { name: "Gas", status: "Bình thường", threshold: "10 ppm" },
          { name: "CO", status: "Nguy hiểm", threshold: "30 ppm" },
        ],
      },
    ],
  },
};



export default function RoomDetail({ params }) {
  const roomId = params.id; // lấy từ route /rooms/:id
  const room = roomsData[roomId];

  if (!room) return <p>Không có dữ liệu phòng này</p>;

  return (
    <div>
      <h2 className="text-2xl font-bold mb-4">Chi tiết phòng {roomId}</h2>

      {room.devices.map((device, idx) => (
        <div key={idx} className="mb-6 p-4 bg-white rounded shadow">
          <div className="flex justify-between items-center mb-2">
            <h3 className="font-bold text-lg">{device.name}</h3>
            <span
              className={`px-2 py-1 rounded text-white ${
                device.status === "Online" ? "bg-green-500" : "bg-gray-500"
              }`}
            >
              {device.status}
            </span>
          </div>

          {/* Sensor Cards */}
          <div className="grid grid-cols-2 gap-4 mt-2">
            {device.sensors.map((sensor, idx2) => (
              <SensorCard key={idx2} sensor={sensor} />
            ))}
          </div>

          {/* Biểu đồ placeholder */}
          <div className="grid grid-cols-2 gap-4 mt-4">
            <div className="p-4 bg-gray-100 rounded shadow h-64 flex items-center justify-center">
              Biểu đồ Nhiệt độ (placeholder)
            </div>
            <div className="p-4 bg-gray-100 rounded shadow h-64 flex items-center justify-center">
              Biểu đồ Nồng độ Khói/Gas (placeholder)
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
