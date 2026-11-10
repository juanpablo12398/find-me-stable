import { describe, it, expect } from 'vitest';
import { AuthService, AvistadorService, AvistamientoService, DesaparecidoService } from '@/services';
import { AuthService as AuthServiceDirect } from '@/services/AuthService';
import { AvistadorService as AvistadorServiceDirect } from '@/services/AvistadorService';
import { AvistamientoService as AvistamientoServiceDirect } from '@/services/AvistamientoService';
import { DesaparecidoService as DesaparecidoServiceDirect } from '@/services/DesaparecidoService';

describe('services/index', () => {
    it('re-exporta los servicios esperados', () => {
        expect(AuthService).toBe(AuthServiceDirect);
        expect(AvistadorService).toBe(AvistadorServiceDirect);
        expect(AvistamientoService).toBe(AvistamientoServiceDirect);
        expect(DesaparecidoService).toBe(DesaparecidoServiceDirect);
    });
});
