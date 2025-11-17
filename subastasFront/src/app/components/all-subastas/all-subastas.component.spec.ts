import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllSubastasComponent } from './all-subastas.component';

describe('AllSubastasComponent', () => {
  let component: AllSubastasComponent;
  let fixture: ComponentFixture<AllSubastasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllSubastasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllSubastasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
