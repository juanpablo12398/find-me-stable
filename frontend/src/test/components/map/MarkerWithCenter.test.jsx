import { describe, it, expect, vi } from 'vitest';
import { render, fireEvent } from '@testing-library/react';
import MarkerWithCenter from '@/components/map/MarkerWithCenter';

// Mocks
const mockMap = {
    flyTo: vi.fn(),
    getZoom: vi.fn().mockReturnValue(14)
};

vi.mock('react-leaflet', () => {
    return {
        useMap: () => mockMap,
        Marker: ({ children, position, icon, eventHandlers }) => (
            <div data-testid="mock-marker" onClick={eventHandlers.click}>
                {children}
            </div>
        )
    };
});

describe('MarkerWithCenter', () => {
    it('should render children within Marker', () => {
        const { getByText } = render(
            <MarkerWithCenter position={[0, 0]} icon={{}}>
                <span>Test Child</span>
            </MarkerWithCenter>
        );

        expect(getByText('Test Child')).toBeDefined();
    });

    it('should call map.flyTo on marker click', () => {
        const { getByTestId } = render(
            <MarkerWithCenter position={[-34.6037, -58.3816]} icon={{}}>
                <span>Test Child</span>
            </MarkerWithCenter>
        );

        const marker = getByTestId('mock-marker');
        fireEvent.click(marker);

        expect(mockMap.getZoom).toHaveBeenCalled();
        expect(mockMap.flyTo).toHaveBeenCalledWith([-34.6037, -58.3816], 14, { duration: 0.5 });
    });
});
