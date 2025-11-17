import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PagosSubastaComponent } from './pagos-subasta.component';

describe('PagosSubastaComponent', () => {
  let component: PagosSubastaComponent;
  let fixture: ComponentFixture<PagosSubastaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PagosSubastaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PagosSubastaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
