import { TestBed } from '@angular/core/testing';

import { InformeVendedorService } from './informe-vendedor.service';

describe('InformeVendedorService', () => {
  let service: InformeVendedorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InformeVendedorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
