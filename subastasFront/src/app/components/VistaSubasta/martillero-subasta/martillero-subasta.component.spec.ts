import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MartilleroSubastaComponent } from './martillero-subasta.component';

describe('MartilleroSubastaComponent', () => {
  let component: MartilleroSubastaComponent;
  let fixture: ComponentFixture<MartilleroSubastaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MartilleroSubastaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MartilleroSubastaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
