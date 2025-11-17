import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PujaAutomaticaComponent } from './puja-automatica.component';

describe('PujaAutomaticaComponent', () => {
  let component: PujaAutomaticaComponent;
  let fixture: ComponentFixture<PujaAutomaticaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PujaAutomaticaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PujaAutomaticaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
