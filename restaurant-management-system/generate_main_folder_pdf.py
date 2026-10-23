from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.pagesizes import A4
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, ListFlowable, ListItem
from reportlab.lib.units import inch
from reportlab.lib import colors
from pathlib import Path

src = Path(r'd:\Study Material\OOP mini project\restaurant-management-system\docs\main-folder-complete-code-explanation.md')
out = Path(r'd:\Study Material\OOP mini project\restaurant-management-system\docs\main-folder-complete-code-explanation.pdf')

text = src.read_text(encoding='utf-8')
lines = text.splitlines()

styles = getSampleStyleSheet()
body = ParagraphStyle('Body', parent=styles['BodyText'], fontSize=10, leading=14, spaceAfter=6)
heading1 = ParagraphStyle('H1', parent=styles['Title'], fontSize=18, leading=22, spaceAfter=16, textColor=colors.HexColor('#0B3D91'))
heading2 = ParagraphStyle('H2', parent=styles['Heading2'], fontSize=14, leading=18, spaceAfter=8, textColor=colors.HexColor('#1F3F8A'))
heading3 = ParagraphStyle('H3', parent=styles['Heading3'], fontSize=11.5, leading=16, spaceAfter=6, textColor=colors.HexColor('#274E13'))

story = []
current_list = []

def flush_list():
    if current_list:
        story.append(ListFlowable(
            [ListItem(Paragraph(item, body), bulletType='bullet', bulletColor=colors.black) for item in current_list],
            bulletType='bullet',
            bulletColor=colors.black,
            leftIndent=18,
            spaceBefore=4,
            spaceAfter=6,
        ))
        current_list.clear()

for raw in lines:
    line = raw.rstrip()
    if not line.strip():
        flush_list()
        story.append(Spacer(1, 6))
        continue
    if line.startswith('# '):
        flush_list(); story.append(Paragraph(line[2:], heading1))
    elif line.startswith('## '):
        flush_list(); story.append(Paragraph(line[3:], heading2))
    elif line.startswith('### '):
        flush_list(); story.append(Paragraph(line[4:], heading3))
    elif line.startswith('- '):
        current_list.append(line[2:])
    elif line.startswith('---'):
        flush_list(); story.append(Spacer(1, 8))
    else:
        flush_list(); story.append(Paragraph(line, body))
flush_list()

doc = SimpleDocTemplate(str(out), pagesize=A4, rightMargin=40, leftMargin=40, topMargin=40, bottomMargin=40)
doc.build(story)
print(f'PDF created: {out}')
