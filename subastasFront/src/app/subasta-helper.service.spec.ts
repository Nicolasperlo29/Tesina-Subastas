import { TestBed } from '@angular/core/testing';

import { SubastaHelperService } from './subasta-helper.service';

describe('SubastaHelperService', () => {
  let service: SubastaHelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubastaHelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
