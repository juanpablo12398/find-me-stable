import { Marker } from "react-leaflet";
import { useMap } from "react-leaflet";

export default function MarkerWithCenter({ children, position, icon }) {
    const map = useMap();

    const handleClick = () => {
        map.flyTo(position, map.getZoom(), {
            duration: 0.5
        });
    };

    return (
        <Marker
            position={position}
            icon={icon}
            eventHandlers={{
                click: handleClick
            }}
        >
            {children}
        </Marker>
    );
}