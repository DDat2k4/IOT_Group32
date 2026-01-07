export default function SensorCard({ sensor }) {
  return (
    <div className="p-4 rounded shadow bg-blue-200 flex flex-col justify-between">
      <h3 className="font-bold mb-2">{sensor.name}</h3>
      <p>Ngưỡng cảnh báo: {sensor.maxValue}</p>
      <p>Trạng thái: {sensor.status}</p>
    </div>
  );
}
