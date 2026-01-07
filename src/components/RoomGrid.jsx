export default function RoomGrid({ rooms }) {
  return (
    <div className="grid grid-cols-4 gap-4">
      {rooms.map(room => (
        <div
          key={room.id}
          className={`p-4 rounded text-center font-bold cursor-pointer
            ${room.level === "normal" ? "bg-green-200" :
              room.level === "warning" ? "bg-yellow-300" : "bg-red-400 animate-pulse"}`}
        >
          Phòng {room.id}
        </div>
      ))}
    </div>
  );
}
