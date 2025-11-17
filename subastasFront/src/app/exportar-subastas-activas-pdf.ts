import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import { SubastaDTO } from './subasta-dto';

export function exportarSubastasActivasPDF(subastas: SubastaDTO[]) {
  const doc = new jsPDF();

  doc.text('Listado de Subastas Activas', 14, 15);

  autoTable(doc, {
    startY: 20,
    head: [['Título', 'Fecha fin', 'Precio inicial', 'Email creador']],
    body: subastas.map(s => [
      s.title,
      new Date(s.fechaFin).toLocaleDateString(),
      `$${s.precioInicial.toFixed(2)}`,
      s.emailCreador
    ])
  });

  doc.save('subastas-activas.pdf');
}